package eus.ibai.jobs.alerts.infrastructure.email;

import java.util.List;

public record TestMimeMessage(List<String> recipients, String subject) {}
