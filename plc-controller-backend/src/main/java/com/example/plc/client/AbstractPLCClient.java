package com.example.plc.client;

/**
 * 抽象PLC客户端类，定义通用的PLC通信接口
 * 用于支持多种PLC协议（如Modbus TCP、OPC UA等）
 */
public abstract class AbstractPLCClient {
    // 设备IP地址
    protected String ip;
    // 设备端口
    protected int port;
    // 连接状态
    protected boolean connected;

    /**
     * 构造函数
     * @param ip 设备IP地址
     * @param port 设备端口
     */
    public AbstractPLCClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.connected = false;
    }

    /**
     * 连接到PLC
     * @throws Exception 连接异常
     */
    public abstract void connect() throws Exception;

    /**
     * 断开与PLC的连接
     */
    public abstract void disconnect();

    /**
     * 检查是否已连接
     * @return 是否已连接
     */
    public abstract boolean isConnected();

    /**
     * 读取离散输入（功能码02）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    public abstract boolean[] readDiscreteInput(int address, int quantity) throws Exception;

    /**
     * 读取线圈（功能码01）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    public abstract boolean[] readCoil(int address, int quantity) throws Exception;

    /**
     * 写入单个线圈（功能码05）
     * @param address 地址
     * @param value 值
     * @throws Exception 写入异常
     */
    public abstract void writeSingleCoil(int address, boolean value) throws Exception;

    /**
     * 读取输入寄存器（功能码04）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    public abstract short[] readInputRegister(int address, int quantity) throws Exception;

    /**
     * 读取保持寄存器（功能码03）
     * @param address 地址
     * @param quantity 数量
     * @return 读取结果
     * @throws Exception 读取异常
     */
    public abstract short[] readHoldingRegister(int address, int quantity) throws Exception;

    /**
     * 写入单个保持寄存器（功能码06）
     * @param address 地址
     * @param value 值
     * @throws Exception 写入异常
     */
    public abstract void writeSingleHoldingRegister(int address, short value) throws Exception;

    /**
     * 写入多个保持寄存器（功能码16）
     * @param address 起始地址
     * @param values 值数组
     * @throws Exception 写入异常
     */
    public abstract void writeMultipleHoldingRegisters(int address, short[] values) throws Exception;

    /**
     * 获取设备IP地址
     * @return 设备IP地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 获取设备端口
     * @return 设备端口
     */
    public int getPort() {
        return port;
    }
}