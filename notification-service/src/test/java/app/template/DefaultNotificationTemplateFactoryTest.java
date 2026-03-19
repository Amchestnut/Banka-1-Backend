package app.template;

import app.dto.EmailTemplate;
import app.exception.BusinessException;
import app.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DefaultNotificationTemplateFactory}.
 *
 * <p>This class verifies the map-backed template-loading variant of the factory. The
 * tests make sure that templates can be read from a map and that missing configuration
 * fails with an explicit domain exception.
 */
class DefaultNotificationTemplateFactoryTest {

    /**
     * Verifies that a template is returned when it is defined in the map.
     *
     * <p>This protects property-based template loading, which is the configuration style used by
     * the running service.
     */
    @Test
    void resolveReturnsTemplateWhenDefined() {
        DefaultNotificationTemplateFactory factory = new DefaultNotificationTemplateFactory(
                Map.of("EMPLOYEE_CREATED", new EmailTemplate("Test Subject", "Test Body"))
        );

        EmailTemplate template = factory.resolve("EMPLOYEE_CREATED");

        assertNotNull(template);
        assertEquals("Test Subject", template.subject());
        assertEquals("Test Body", template.bodyTemplate());
    }

    /**
     * Verifies that resolving a template fails with a clear business exception when the template
     * is not configured.
     *
     * <p>This prevents silent fallback behavior that would hide broken notification
     * configuration.
     */
    @Test
    void resolveThrowsWhenTemplateNotDefined() {
        DefaultNotificationTemplateFactory factory = new DefaultNotificationTemplateFactory(Map.of());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> factory.resolve("EMPLOYEE_CREATED"));
        assertEquals(ErrorCode.EMAIL_CONTENT_RESOLUTION_FAILED, exception.getErrorCode());
        assertEquals("No template defined for notification type: EMPLOYEE_CREATED", exception.getDetails());
    }
}
