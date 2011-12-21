package org.androidrobotics.analysis.astAnalyzer;

import org.androidrobotics.model.ConstructorInjectionPoint;
import org.androidrobotics.model.FieldInjectionPoint;
import org.androidrobotics.model.MethodInjectionPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * @author John Ericksen
 */
public class ASTInjectionAspect {

    private Set<ConstructorInjectionPoint> constructorInjectionPoints = new HashSet<ConstructorInjectionPoint>();
    private Set<MethodInjectionPoint> methodInjectionPoints = new HashSet<MethodInjectionPoint>();
    private Set<FieldInjectionPoint> fieldInjectionPoints = new HashSet<FieldInjectionPoint>();

    public void add(ConstructorInjectionPoint constructorInjectionPoint) {
        constructorInjectionPoints.add(constructorInjectionPoint);
    }

    public void add(MethodInjectionPoint methodInjectionPoint) {
        methodInjectionPoints.add(methodInjectionPoint);
    }

    public void add(FieldInjectionPoint fieldInjectionPoint) {
        fieldInjectionPoints.add(fieldInjectionPoint);
    }

    public ConstructorInjectionPoint getConstructorInjectionPoint() {
        return constructorInjectionPoints.iterator().next();
    }

    public Set<ConstructorInjectionPoint> getConstructorInjectionPoints() {
        return constructorInjectionPoints;
    }

    public Set<MethodInjectionPoint> getMethodInjectionPoints() {
        return methodInjectionPoints;
    }

    public Set<FieldInjectionPoint> getFieldInjectionPoints() {
        return fieldInjectionPoints;
    }
}