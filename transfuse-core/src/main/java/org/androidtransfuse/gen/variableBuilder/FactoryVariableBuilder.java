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
package org.androidtransfuse.gen.variableBuilder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.FactoryGenerator;
import org.androidtransfuse.gen.InjectionBuilderContext;
import org.androidtransfuse.gen.variableDecorator.TypedExpressionFactory;
import org.androidtransfuse.model.InjectionNode;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class FactoryVariableBuilder extends ConsistentTypeVariableBuilder {

    private final ASTType factoryType;
    private final ClassGenerationUtil generationUtil;

    @Inject
    public FactoryVariableBuilder(/*@Assisted*/ ASTType factoryType,
                                  TypedExpressionFactory typedExpressionFactory,
                                  ClassGenerationUtil generationUtil) {
        super(factoryType, typedExpressionFactory);
        this.factoryType = factoryType;
        this.generationUtil = generationUtil;
    }

    @Override
    public JExpression buildExpression(InjectionBuilderContext context, InjectionNode injectionNode) {


        PackageClass factoryClassName = FactoryGenerator.getFactoryName(factoryType);

        JClass factoryRef = generationUtil.ref(factoryClassName);

        return JExpr._new(factoryRef).arg(context.getScopeVar());
    }
}
