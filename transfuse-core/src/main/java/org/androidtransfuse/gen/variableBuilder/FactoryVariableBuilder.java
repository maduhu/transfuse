/**
 * Copyright 2013 John Ericksen
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

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.FactoriesGenerator;
import org.androidtransfuse.gen.InjectionBuilderContext;
import org.androidtransfuse.gen.variableDecorator.TypedExpressionFactory;
import org.androidtransfuse.model.InjectionNode;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class FactoryVariableBuilder extends ConsistentTypeVariableBuilder {

    private final ASTType factoryType;
    private final JCodeModel codeModel;

    @Inject
    public FactoryVariableBuilder(/*@Assisted*/ ASTType factoryType, TypedExpressionFactory typedExpressionFactory, JCodeModel codeModel) {
        super(factoryType, typedExpressionFactory);
        this.factoryType = factoryType;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression buildExpression(InjectionBuilderContext context, InjectionNode injectionNode) {

        JExpression factoryClass = codeModel.ref(factoryType.getName()).dotclass();

        return codeModel.ref(FactoriesGenerator.FACTORIES_NAME.getFullyQualifiedName())
                .staticInvoke(FactoriesGenerator.GET_METHOD)
                .arg(factoryClass)
                .arg(context.getScopeVar());
    }
}