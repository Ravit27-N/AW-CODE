package com.tessi.cxm.pfl.ms5.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.tessi.cxm.pfl.ms5.dto.CreateUserRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.UserInfoRequestUpdatePasswordDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, ConstraintValidatorContext constraintValidatorContext) {

       if(obj instanceof UserInfoRequestUpdatePasswordDto) {
           final UserInfoRequestUpdatePasswordDto passwordObj = (UserInfoRequestUpdatePasswordDto) obj;
           return passwordObj.getNewPassword().equals(passwordObj.getConfirmPassword());
       }

        if(obj instanceof CreateUserRequestDTO) {
            final CreateUserRequestDTO passwordObj = (CreateUserRequestDTO) obj;
            return passwordObj.getPassword().equals(passwordObj.getConfirmedPassword());
        }

       return false;
    }

}