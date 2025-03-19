package whilter.ai.ads_manager.model;
import lombok.Data;

@Data
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
}
