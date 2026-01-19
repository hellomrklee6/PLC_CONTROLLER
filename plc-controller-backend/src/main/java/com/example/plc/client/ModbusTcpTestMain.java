package com.example.plc.client;

import com.github.xingshuangs.iot.protocol.modbus.service.ModbusTcp;

/**
 * 在主目录中创建的ModbusTcp测试类
 */
public class ModbusTcpTestMain {
    public static void main(String[] args) {
        ModbusTcp modbusTcp = null;
        try {
            // 创建ModbusTcp实例，连接到本地的ModbusTCP Slave
            modbusTcp = new ModbusTcp("127.0.0.1", 55554);
            modbusTcp.connect(); // 使用无参connect方法
            
            System.out.println("连接成功，开始写入数据...");
            
            // 测试写入保持寄存器（功能码06）- 地址2（alias-00000的第2行）
            int address = 2;
            short value = 1;
            modbusTcp.writeInt16(address, value);
            System.out.println("写入保持寄存器成功 - 地址: " + address + ", 值: " + value);
            
            // 读取写入的值，验证是否写入成功
            short readValue = modbusTcp.readInt16(address);
            System.out.println("读取保持寄存器成功 - 地址: " + address + ", 值: " + readValue);
            
        } catch (Exception e) {
            System.err.println("发生异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (modbusTcp != null) {
                try {
                    modbusTcp.close(); // 使用close方法代替disconnect方法
                    System.out.println("断开连接成功");
                } catch (Exception e) {
                    System.err.println("断开连接时发生异常: " + e.getMessage());
                }
            }
        }
    }
}