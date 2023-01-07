package com.ming.mingcommerce.member.exception;

public class MemberException extends RuntimeException {
    protected MemberException() {
        super();
    }

    protected MemberException(String message) {
        super(message);
    }

    public static class MemberRegisterFailedException extends MemberException {
        protected MemberRegisterFailedException() {
            super();
        }

        public MemberRegisterFailedException(String message) {
            super(message);
        }
    }
}
