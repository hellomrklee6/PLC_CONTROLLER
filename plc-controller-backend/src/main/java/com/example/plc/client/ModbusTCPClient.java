package com.example.plc.client;

import com.github.xingshuangs.iot.protocol.modbus.service.ModbusTcp;
import com.example.plc.service.PlcConnectionLogService;
import java.util.concurrent.TimeoutException;

/**
 * Modbus TCP PLC客户端类，实现Modbus TCP协议的PLC通信
 */
public class ModbusTCPClient extends AbstractPLCClient {

    private ModbusTcp plc;
    private final PlcConnectionLogService logService;

    public ModbusTCPClient(String ip, int port) {
        super(ip, port);
        this.plc = null;
        this.logService = null;
    }
    
    /**
     * 使用日志服务的构造函数
     */
    public ModbusTCPClient(String ip, int port, PlcConnectionLogService logService) {
        super(ip, port);
        this.plc = null;
        // 不使用日志服务，避免重复日志记录
        this.logService = null;
    }

    /**
     * 连接到PLC
     * @throws Exception 连接异常
     */
    public synchronized void connect() throws Exception {
        if (connected) {
            return;
        }

        try {
            // 真正建立TCP连接到PLC
            log("[PLCClient] 正在建立TCP连接到PLC: " + ip + ":" + port);
            // 使用iot-communication库的ModbusTcp建立连接
            this.plc = new ModbusTcp(ip, port);
            this.plc.connect(); // 使用无参connect方法
            connected = true;
            log("[PLCClient] TCP连接成功: " + ip + ":" + port);
        } catch (Exception e) {
            connected = false;
            log("[PLCClient] 连接失败: " + ip + ":" + port + ", 错误: " + e.getMessage());
            throw new Exception("连接PLC失败: " + ip + ":" + port, e);
        }
    }

    /**
     * 断开与PLC的连接
     */
    public synchronized void disconnect() {
        if (!connected) {
            return;
        }

        try {
            log("[PLCClient] 正在断开与PLC的连接: " + ip + ":" + port);
            if (this.plc != null) {
                this.plc.close(); // 使用close方法代替disconnect方法
                this.plc = null;
            }
            connected = false;
            log("[PLCClient] 断开成功: " + ip + ":" + port);
        } catch (Exception e) {
            log("[PLCClient] 断开连接时发生异常: " + e.getMessage());
        } finally {
            connected = false;
            this.plc = null;
        }
    }

    /**
     * 检查是否已连接
     * @return 是否已连接
     */
    public boolean isConnected() {
        return connected && this.plc != null;
    }
    
    /**
     * 输出日志到日志服务和控制台
     */
    private void log(String message) {
        // 如果logService可用，通过日志服务记录
        if (logService != null) {
            logService.addLog(message);
        }
    }

    /**
     * 读取离散输入（功能码02）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    @Override
    public boolean[] readDiscreteInput(int address, int quantity) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp读取离散输入
        boolean[] result = new boolean[quantity];
        for (int i = 0; i < quantity; i++) {
            // 使用readInt16读取并转换为boolean
            short value = this.plc.readInt16(address + i);
            result[i] = (value != 0);
        }
        return result;
    }

    /**
     * 读取线圈（功能码01）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    @Override
    public boolean[] readCoil(int address, int quantity) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp读取线圈
        boolean[] result = new boolean[quantity];
        for (int i = 0; i < quantity; i++) {
            // 使用readInt16读取并转换为boolean
            short value = this.plc.readInt16(address + i);
            result[i] = (value != 0);
        }
        return result;
    }

    /**
     * 读取输入寄存器（功能码04）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    @Override
    public short[] readInputRegister(int address, int quantity) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp读取输入寄存器
        short[] result = new short[quantity];
        for (int i = 0; i < quantity; i++) {
            result[i] = this.plc.readInt16(address + i);
        }
        return result;
    }

    /**
     * 读取保持寄存器（功能码03）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    @Override
    public short[] readHoldingRegister(int address, int quantity) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp读取保持寄存器
        short[] result = new short[quantity];
        for (int i = 0; i < quantity; i++) {
            result[i] = this.plc.readInt16(address + i);
        }
        return result;
    }

    /**
     * 写入单个线圈（功能码05）
     * @param address 地址
     * @param value 值
     * @throws Exception 写入异常
     */
    @Override
    public void writeSingleCoil(int address, boolean value) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp写入单个线圈
        // 将boolean值转换为short值（true=1，false=0）
        short shortValue = (short) (value ? 1 : 0);
        this.plc.writeInt16(address, shortValue);
    }

    /**
     * 写入单个保持寄存器（功能码06）
     * @param address 地址
     * @param value 值
     * @throws Exception 写入异常
     */
    @Override
    public void writeSingleHoldingRegister(int address, short value) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp写入单个保持寄存器
        this.plc.writeInt16(address, value);
    }

    /**
     * 写入多个保持寄存器（功能码16）
     * @param address 起始地址
     * @param values 值数组
     * @throws Exception 写入异常
     */
    @Override
    public void writeMultipleHoldingRegisters(int address, short[] values) throws Exception {
        ensureConnected();
        // 使用iot-communication库的ModbusTcp写入多个保持寄存器
        for (int i = 0; i < values.length; i++) {
            this.plc.writeInt16(address + i, values[i]);
        }
    }

    /**
     * 确保连接有效
     * @throws Exception 连接异常
     */
    private void ensureConnected() throws Exception {
        if (!isConnected()) {
            throw new TimeoutException("PLC连接已断开");
        }
    }
}