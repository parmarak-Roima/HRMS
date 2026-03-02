package com.HRMS.HRMS.dto.EmailDtos;

import lombok.*;

@AllArgsConstructor
@Data
@Getter
@Setter@NoArgsConstructor
public class ReferralEmailDto {
    private String rolePosition;
    private String candidateName;
    private String candidateEmail;
    private String referrerName;
    private String referrerNote;
}
