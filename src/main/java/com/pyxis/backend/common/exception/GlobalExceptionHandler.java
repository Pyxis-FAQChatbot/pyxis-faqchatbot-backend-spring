package com.pyxis.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /* =========================
     * 1) CustomException (프로젝트 공통)
     * ========================= */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex, HttpServletRequest req) {
        ErrorType type = ex.getType();
        logWarn(type.getStatus(), type.name(), ex.getMessage(), req);

        Map<String, Object> body = baseBody(type.name(), type.getDescription());
        putIfNotEmpty(body, ex.getDetails());

        return ResponseEntity.status(type.getStatus()).body(body);
    }

    /* =========================
     * 2) @Valid 바인딩 에러 (RequestBody DTO)
     * ========================= */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> missingFields = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .distinct()
                .toList();

        Map<String, Object> body = baseBody("BAD_REQUEST", "요청 값이 올바르지 않습니다.");
        putIfNotEmpty(body, Map.of("missing_fields", missingFields));
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, String> toViolation(FieldError fe) {
        return Map.of("field", fe.getField(), "reason", Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid"));
    }

    /* =========================
     * 3) 쿼리 파라미터 누락
     * ========================= */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = baseBody("BAD_REQUEST", "필수 파라미터가 누락되었습니다.");
        putIfNotEmpty(body, Map.of("missing_fields", List.of(ex.getParameterName())));

        logWarn(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
        return ResponseEntity.badRequest().body(body);
    }

    /* =========================
     * 4) JSON 파싱 오류
     * ========================= */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = baseBody("BAD_REQUEST", "요청 본문을 해석할 수 없습니다.");
        logWarn(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
        return ResponseEntity.badRequest().body(body);
    }

    /* =========================
     * 5) @Validated 계층 제약 위반(쿼리/경로 등)
     * ========================= */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<Map<String, String>> violations = ex.getConstraintViolations().stream().map(v -> Map.of("field", String.valueOf(v.getPropertyPath()), "reason", Optional.ofNullable(v.getMessage()).orElse("invalid"))).toList();

        Map<String, Object> body = baseBody("BAD_REQUEST", "요청 데이터가 올바르지 않습니다.");
        putIfNotEmpty(body, Map.of("violations", violations));

        logWarn(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req);
        return ResponseEntity.badRequest().body(body);
    }

    /* =========================
     * 7) 404 (NoHandlerFound) – 설정에 따라 동작
     * ========================= */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = baseBody("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.");
        logWarn(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /* =========================
     * 8) 405 메서드 불가
     * ========================= */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = baseBody("METHOD_NOT_ALLOWED", "허용되지 않은 메서드입니다.");

        putIfNotEmpty(body, Map.of("supported", Optional.ofNullable(ex.getSupportedHttpMethods()).map(set -> set.stream().map(HttpMethod::name).toList()).orElse(List.of())));


        logWarn(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    /* =========================
     * 9) 그 외 미처리 예외 → 500
     * ========================= */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("[UNEXPECTED] {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);

        Map<String, Object> body = baseBody("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /* =========================
     * 공통 유틸
     * ========================= */
    private Map<String, Object> baseBody(String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", OffsetDateTime.now().toString());
        return body;
    }

    private void putIfNotEmpty(Map<String, Object> body, Object details) {
        if (details == null) return;

        if (details instanceof Map<?, ?> map && !map.isEmpty()) {
            body.put("details", map);
        } else if (details instanceof Collection<?> list && !list.isEmpty()) {
            body.put("details", list);
        }

    }



    private void logWarn(HttpStatus status, String code, String msg, WebRequest request) {
        String path = Optional.ofNullable(request.getDescription(false)).map(s -> s.replace("uri=", "")).orElse("-");
        log.warn("[{}] {} - {} (path={})", status.value(), code, msg, path);
    }

    private void logWarn(HttpStatus status, String code, String msg, HttpServletRequest req) {
        log.warn("[{}] {} - {} (path={})", status.value(), code, msg, req.getRequestURI());
    }
}
