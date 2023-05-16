package dev.fromnowon.mfiserver.exception;

/**
 * 业务异常类
 *
 * @author hansai
 */
public class BizException extends RuntimeException {

    /**
     * 错误码
     */
    private String code;

    /**
     * 构造一个没有错误信息的 <code>BizException</code>
     */
    public BizException() {
        super();
    }

    /**
     * 使用指定的 Throwable 和 Throwable.toString() 作为异常信息来构造 BizException
     *
     * @param cause 错误原因， 通过 Throwable.getCause() 方法可以获取传入的 cause信息
     */
    public BizException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用错误信息 message 构造 BizException
     *
     * @param message 错误信息
     */
    public BizException(String message) {
        super(message);
    }

    /**
     * 使用错误码和错误信息构造 BizException
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用错误信息和 Throwable 构造 BizException
     *
     * @param message 错误信息
     * @param cause   错误原因
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code    错误码
     * @param message 错误信息
     * @param cause   错误原因
     */
    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * @param errorCode ErrorCode
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * @param errorCode ErrorCode
     * @param cause     错误原因
     */
    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

}