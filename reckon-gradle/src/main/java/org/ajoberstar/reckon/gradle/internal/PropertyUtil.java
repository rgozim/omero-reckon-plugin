package org.ajoberstar.reckon.gradle.internal;

import org.gradle.api.Project;

import java.util.Optional;

class PropertyUtil {

    static Optional<String> findProperty(Project project, String value, Object fallback) {
        Object result = Optional.ofNullable(project.findProperty(value)).orElse(fallback);
        return Optional.ofNullable(result).map(Object::toString);
    }

}
