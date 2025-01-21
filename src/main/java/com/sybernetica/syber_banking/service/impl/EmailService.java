package com.sybernetica.syber_banking.service.impl;

import com.sybernetica.syber_banking.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
