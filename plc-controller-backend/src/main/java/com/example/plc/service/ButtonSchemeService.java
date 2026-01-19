package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import com.example.plc.entity.ButtonScheme;
import com.example.plc.repository.ButtonSchemeRepository;
import com.example.plc.repository.PlcAddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ButtonSchemeService {

    private final ButtonSchemeRepository schemeRepository;
    private final PlcAddressRepository addressRepository;

    public ButtonSchemeService(ButtonSchemeRepository schemeRepository, PlcAddressRepository addressRepository) {
        this.schemeRepository = schemeRepository;
        this.addressRepository = addressRepository;
    }

    public List<ButtonScheme> findAll() {
        return schemeRepository.findAll();
    }

    public Optional<ButtonScheme> findById(Long id) {
        return schemeRepository.findById(id);
    }

    public ButtonScheme save(ButtonScheme scheme) {
        return schemeRepository.save(scheme);
    }

    /**
     * 删除按钮方案
     * @param id 按钮方案ID
     * @return 是否删除成功
     */
    public boolean deleteById(Long id) {
        // 检查是否有地址引用该按钮方案
        List<PlcAddress> addresses = addressRepository.findByButtonSchemeId(id);
        if (!addresses.isEmpty()) {
            return false;
        }
        
        // 删除按钮方案
        schemeRepository.deleteById(id);
        return true;
    }
}
