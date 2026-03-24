package com.test.demo.service;

import com.test.demo.dto.AccessControlResponseDTO;

public interface AccessControlService {

    AccessControlResponseDTO getUserAccessControl(Long userId);
}
