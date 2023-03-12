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

    public static class EmailDuplicatedException extends MemberException {
        protected EmailDuplicatedException() {
            super();
        }

        public EmailDuplicatedException(String message) {
            super(message);
        }
    }

    public static class MemberEmailNotFoundException extends MemberException {
        public MemberEmailNotFoundException(String message) {
            super(message);
        }
    }

    public static class CurrentlyInUseEmailException extends MemberException {
        public CurrentlyInUseEmailException(String message) {
            super(message);
        }
    }

    public static class ExceedEmailTokenIssue extends MemberException {
        public ExceedEmailTokenIssue(String message) {
            super(message);
        }
    }


    public static class WrongTokenException extends MemberException {
        public WrongTokenException(String message) {
            super(message);
        }
    }
}
