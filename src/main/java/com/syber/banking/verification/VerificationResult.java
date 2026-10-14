package com.syber.banking.verification;

import com.syber.banking.entity.VerificationStatus;

public record VerificationResult (VerificationStatus status, String source, String reason) {

    public static VerificationResult verified(String source){
        return new VerificationResult(VerificationStatus.VERIFIED, source, null);
    }

    public static VerificationResult rejected(String reason){
        return new VerificationResult(VerificationStatus.REJECTED, "AUTO_FORMAT_ONLY", reason);
    }

    public static VerificationResult pending(){
        return new VerificationResult(VerificationStatus.PENDING, null, null);
    }
}
