/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.adapter;

import org.androidtransfuse.adapter.classes.LazyClassParameterBuilder;
import org.androidtransfuse.adapter.classes.LazyParametrizedTypeParameterBuilder;
import org.androidtransfuse.adapter.element.ASTElementAnnotation;
import org.androidtransfuse.adapter.element.LazyElementParameterBuilder;
import org.androidtransfuse.annotations.Factory;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.DeclaredType;
import java.lang.reflect.ParameterizedType;

/**
 * Factory creating an ASTElementAnnotation
 *
 * @author John Ericksen
 */
@Factory
public interface ASTFactory {

    ASTElementAnnotation buildASTElementAnnotation(AnnotationMirror annotationMirror, ASTType type);

    LazyParametrizedTypeParameterBuilder buildParameterBuilder(ParameterizedType genericType);

    LazyClassParameterBuilder buildParameterBuilder(Class type);

    LazyElementParameterBuilder buildParameterBuilder(DeclaredType declaredType);

    LazyASTTypeParameterBuilder buildParameterBuilder(ASTType type);

    ASTGenericTypeWrapper buildGenericTypeWrapper(ASTType astType, LazyTypeParameterBuilder lazyTypeParameterBuilder);
}
