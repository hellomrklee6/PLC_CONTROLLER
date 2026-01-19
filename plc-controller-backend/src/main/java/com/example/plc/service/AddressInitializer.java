package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class AddressInitializer {

    private final PlcAddressService addressService;

    public AddressInitializer(PlcAddressService addressService) {
        this.addressService = addressService;
    }

    // @PostConstruct
    public void initializeAddresses() {
        List<PlcAddress> addresses = createOutputAddresses();
        
        System.out.println("[AddressInitializer] 开始初始化输出地址...");
        System.out.println("[AddressInitializer] 准备添加 " + addresses.size() + " 个地址");
        
        for (PlcAddress address : addresses) {
            try {
                addressService.save(address);
                System.out.println("[AddressInitializer] 添加地址成功: " + address.getName() + " (" + address.getAddress() + ")");
            } catch (Exception e) {
                System.out.println("[AddressInitializer] 添加地址失败: " + address.getName() + " - " + e.getMessage());
            }
        }
        
        System.out.println("[AddressInitializer] 地址初始化完成");
    }

    private List<PlcAddress> createOutputAddresses() {
        List<PlcAddress> addresses = new ArrayList<>();
        
        // 添加输出地址
        addresses.add(createAddress("心跳", "3x3500", PlcAddress.AddressType.INPUT, PlcAddress.DataType.NUMBER, "0：设备未正常运行  1：设备正常运行"));
        addresses.add(createAddress("发送报警信号", "3x3501", PlcAddress.AddressType.INPUT, PlcAddress.DataType.NUMBER, "0：报警器无响应（复位） 2：报警器响 4：报警器红灯亮 6：报警器黄灯亮 8：报警器绿灯亮"));
        addresses.add(createAddress("待检车辆的类型", "3x3502", PlcAddress.AddressType.INPUT, PlcAddress.DataType.NUMBER, "0：无车 1：JP360 2:JP560 3：U725 4:P789"));
        addresses.add(createAddress("待检车辆_颜色", "3x3503", PlcAddress.AddressType.INPUT, PlcAddress.DataType.NUMBER, "1：白色   2：黑色  3：彩色"));
        
        // // VIN码地址 (3504-3514)
        // for (int i = 3504; i <= 3514; i++) {
        //     addresses.add(createAddress("待检车辆_VIN码_" + (i - 3503), "3x" + i, PlcAddress.AddressType.INPUT, PlcAddress.DataType.NUMBER, "VIN码第" + (i - 3503) + "位"));
        // }
        
        addresses.add(createAddress("主程序启动", "3x3515", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "1：启用功能  0:不启用"));
        addresses.add(createAddress("主程序停止", "3x3516", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "1：启用功能  0:不启用"));
        addresses.add(createAddress("主程序初始化", "3x3517", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "1：启用功能  0:不启用"));
        addresses.add(createAddress("主程序故障复位", "3x3518", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "1：启用功能  0:不启用"));
        addresses.add(createAddress("备用功能", "3x3519", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "1：启用功能  0:不启用"));
        addresses.add(createAddress("拖动示教", "3x3520", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("机器人移动至原点", "3x3521", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("开始程序", "3x3522", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("停止程序", "3x3523", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("暂停程序", "3x3524", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("弹窗解除", "3x3525", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("机器人上电", "3x3526", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("机器人断电", "3x3527", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("恢复程序", "3x3528", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("解除保护性停止", "3x3529", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("地轨回原点", "3x3530", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("备用1", "3x3531", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("备用2", "3x3532", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("备用3", "3x3533", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("备用4", "3x3534", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人1    1：启用功能  0:不启用"));
        addresses.add(createAddress("备用5", "3x3535", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("拖动示教", "3x3536", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("机器人移动至原点", "3x3537", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("开始程序", "3x3538", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("停止程序", "3x3539", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("暂停程序", "3x3540", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("弹窗解除", "3x3541", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("机器人上电", "3x3542", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("机器人断电", "3x3543", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("恢复程序", "3x3544", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("解除保护性停止", "3x3545", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        addresses.add(createAddress("地轨回原点", "3x3546", PlcAddress.AddressType.OUTPUT, PlcAddress.DataType.NUMBER, "机器人2    1：启用功能  0:不启用"));
        
        return addresses;
    }

    private PlcAddress createAddress(String name, String address, PlcAddress.AddressType type, PlcAddress.DataType dataType, String description) {
        PlcAddress plcAddress = new PlcAddress();
        plcAddress.setName(name);
        plcAddress.setAddress(address);
        plcAddress.setType(type);
        plcAddress.setDataType(dataType);
        plcAddress.setDescription(description);
        return plcAddress;
    }
}