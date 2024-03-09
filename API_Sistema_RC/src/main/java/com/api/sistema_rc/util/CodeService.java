package com.api.sistema_rc.util;

import com.api.sistema_rc.repository.KanbanRepository;
import com.api.sistema_rc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CodeService {
    @Autowired
    private KanbanRepository kanbanRepository;
    @Autowired
    private UserRepository userRepository;
    public String generateKanbanCode(Integer length){
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        boolean isCode;
        do{
            for (int i = 0; i < length; i++) {
                int digit = random.nextInt(length);
                code.append(digit);
            }
            isCode = kanbanRepository.findByVersion(code.toString()).isPresent();
        }while(isCode);
        return code.toString();
    }
    public String generateUserCodeVerification(Integer length){
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        boolean isCode;
        do{
            for (int i = 0; i < length; i++) {
                int digit = random.nextInt(length);
                code.append(digit);
            }
            isCode = userRepository.findByCodeToVerify(code.toString()).isPresent();
        }while(isCode);
        return code.toString();
    }
    public String generateUserCodeSwitch(Integer length){
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        boolean isCode;
        do{
            for (int i = 0; i < length; i++) {
                int digit = random.nextInt(length);
                code.append(digit);
            }
            isCode = userRepository.findByCodeToSwitch(code.toString()).isPresent();
        }while(isCode);
        return code.toString();
    }
}
