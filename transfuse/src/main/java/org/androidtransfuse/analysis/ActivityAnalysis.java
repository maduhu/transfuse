package org.androidtransfuse.analysis;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import org.androidtransfuse.analysis.adapter.ASTType;
import org.androidtransfuse.analysis.adapter.ASTTypeBuilderVisitor;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.gen.AndroidComponentDescriptor;
import org.androidtransfuse.gen.ComponentBuilder;
import org.androidtransfuse.gen.InjectionNodeBuilderRepository;
import org.androidtransfuse.gen.InjectionNodeBuilderRepositoryFactory;
import org.androidtransfuse.gen.variableBuilder.ApplicationVariableInjectionNodeBuilder;
import org.androidtransfuse.gen.variableBuilder.ContextVariableInjectionNodeBuilder;
import org.androidtransfuse.gen.variableBuilder.ResourcesInjectionNodeBuilder;
import org.androidtransfuse.model.InjectionNode;
import org.androidtransfuse.model.PackageClass;
import org.androidtransfuse.model.manifest.Action;
import org.androidtransfuse.model.manifest.Category;
import org.androidtransfuse.model.manifest.IntentFilter;
import org.androidtransfuse.util.TypeMirrorUtil;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity related Analysis
 *
 * @author John Ericksen
 */
public class ActivityAnalysis {

    private InjectionPointFactory injectionPointFactory;
    private Provider<ContextVariableInjectionNodeBuilder> contextVariableBuilderProvider;
    private InjectionNodeBuilderRepositoryFactory variableBuilderRepositoryFactory;
    private Provider<ResourcesInjectionNodeBuilder> resourcesInjectionNodeBuilderProvider;
    private Provider<ApplicationVariableInjectionNodeBuilder> applicationVariableBuilderProvider;
    private Provider<org.androidtransfuse.model.manifest.Activity> manifestActivityProvider;
    private Provider<IntentFilter> intentFilterProvider;
    private Provider<Action> actionProvider;
    private Provider<Category> categoryProvider;
    private InjectionNodeBuilderRepository injectionNodeBuilders;
    private ActivityComponentBuilderRepository activityComponentBuilderRepository;
    private AnalysisContextFactory analysisContextFactory;
    private Provider<ASTTypeBuilderVisitor> astTypeBuilderVisitorProvider;

    @Inject
    public ActivityAnalysis(InjectionPointFactory injectionPointFactory,
                            Provider<ContextVariableInjectionNodeBuilder> contextVariableBuilderProvider,
                            InjectionNodeBuilderRepositoryFactory variableBuilderRepositoryFactory,
                            Provider<ResourcesInjectionNodeBuilder> resourcesInjectionNodeBuilderProvider,
                            Provider<ApplicationVariableInjectionNodeBuilder> applicationVariableBuilderProvider,
                            Provider<org.androidtransfuse.model.manifest.Activity> manifestActivityProvider,
                            Provider<Category> categoryProvider,
                            Provider<Action> actionProvider,
                            Provider<IntentFilter> intentFilterProvider,
                            InjectionNodeBuilderRepository injectionNodeBuilders,
                            ActivityComponentBuilderRepository activityComponentBuilderRepository,
                            AnalysisContextFactory analysisContextFactory, Provider<ASTTypeBuilderVisitor> astTypeBuilderVisitorProvider, Provider<ASTTypeBuilderVisitor> typeBuilderVisitorProvider) {
        this.injectionPointFactory = injectionPointFactory;
        this.contextVariableBuilderProvider = contextVariableBuilderProvider;
        this.variableBuilderRepositoryFactory = variableBuilderRepositoryFactory;
        this.resourcesInjectionNodeBuilderProvider = resourcesInjectionNodeBuilderProvider;
        this.applicationVariableBuilderProvider = applicationVariableBuilderProvider;
        this.manifestActivityProvider = manifestActivityProvider;
        this.categoryProvider = categoryProvider;
        this.actionProvider = actionProvider;
        this.intentFilterProvider = intentFilterProvider;
        this.injectionNodeBuilders = injectionNodeBuilders;
        this.activityComponentBuilderRepository = activityComponentBuilderRepository;
        this.analysisContextFactory = analysisContextFactory;
        this.astTypeBuilderVisitorProvider = astTypeBuilderVisitorProvider;
        this.astTypeBuilderVisitorProvider = astTypeBuilderVisitorProvider;
    }

    public AndroidComponentDescriptor analyzeElement(ASTType input, AnalysisRepository analysisRepository, org.androidtransfuse.model.manifest.Application application) {

        final Activity activityAnnotation = input.getAnnotation(Activity.class);
        final Layout layoutAnnotation = input.getAnnotation(Layout.class);
        final LayoutHandler layoutHandlerAnnotation = input.getAnnotation(LayoutHandler.class);
        final IntentFilters intentFilters = input.getAnnotation(IntentFilters.class);

        TypeMirror type = TypeMirrorUtil.getTypeMirror(new Runnable() {
            public void run() {
                activityAnnotation.type();
            }
        });

        String activityType;

        if (type != null) {
            activityType = type.toString();
        } else {
            activityType = android.app.Activity.class.getName();
        }

        String name = input.getName();
        String packageName = name.substring(0, name.lastIndexOf('.'));
        String delegateName = name.substring(name.lastIndexOf('.') + 1);

        PackageClass activityClassName;
        if (StringUtils.isBlank(activityAnnotation.name())) {
            activityClassName = new PackageClass(packageName, delegateName + "Activity");
        } else {
            activityClassName = new PackageClass(packageName, activityAnnotation.name());
        }

        Integer layout = null;
        if (layoutAnnotation != null) {
            layout = layoutAnnotation.value();
        }

        AnalysisContext context = analysisContextFactory.buildAnalysisContext(analysisRepository, buildVariableBuilderMap(type));

        InjectionNode layoutHandlerInjectionNode = null;
        if (layoutHandlerAnnotation != null) {
            TypeMirror layoutHandlerType = TypeMirrorUtil.getTypeMirror(new Runnable() {
                public void run() {
                    layoutHandlerAnnotation.value();
                }
            });

            if (layoutHandlerType != null) {
                ASTType layoutHandlerASTType = layoutHandlerType.accept(astTypeBuilderVisitorProvider.get(), null);
                layoutHandlerInjectionNode = injectionPointFactory.buildInjectionNode(layoutHandlerASTType, context);
            }
        }

        AndroidComponentDescriptor activityDescriptor = new AndroidComponentDescriptor(activityType, activityClassName);
        InjectionNode injectionNode = injectionPointFactory.buildInjectionNode(input, context);

        //application generation profile
        setupActivityProfile(activityType, activityDescriptor, injectionNode, layout, layoutHandlerInjectionNode);

        //add manifest elements
        setupManifest(activityClassName.getFullyQualifiedName(), activityAnnotation.label(), intentFilters, application);

        return activityDescriptor;
    }

    private void setupManifest(String name, String label, IntentFilters intentFilters, org.androidtransfuse.model.manifest.Application application) {
        org.androidtransfuse.model.manifest.Activity manifestActivity = manifestActivityProvider.get();

        manifestActivity.setName(name);
        manifestActivity.setLabel(StringUtils.isBlank(label) ? null : label);
        manifestActivity.setIntentFilters(buildIntentFilters(intentFilters));

        if (application.getActivities() == null) {
            application.setActivities(new ArrayList<org.androidtransfuse.model.manifest.Activity>());
        }

        application.getActivities().add(manifestActivity);
    }

    private void setupActivityProfile(String activityType, AndroidComponentDescriptor activityDescriptor, InjectionNode injectionNode, Integer layout, InjectionNode layoutHandlerInjectionNode) {
        ComponentBuilder activityComponentBuilder = activityComponentBuilderRepository.buildComponentBuilder(activityType, injectionNode, layout, layoutHandlerInjectionNode);

        activityDescriptor.getComponentBuilders().add(activityComponentBuilder);
    }

    private List<IntentFilter> buildIntentFilters(IntentFilters intentFilters) {
        List<IntentFilter> convertedIntentFilters = new ArrayList<IntentFilter>();

        if (intentFilters != null) {

            IntentFilter intentFilter = intentFilterProvider.get();
            convertedIntentFilters.add(intentFilter);

            for (Intent intentAnnotation : intentFilters.value()) {
                switch (intentAnnotation.type()) {
                    case ACTION:
                        Action action = actionProvider.get();
                        action.setName(intentAnnotation.name());
                        intentFilter.getActions().add(action);
                        break;
                    case CATEGORY:
                        Category category = categoryProvider.get();
                        category.setName(intentAnnotation.name());
                        intentFilter.getCategories().add(category);
                        break;
                    default:
                        //noop
                        break;
                }
            }
        }

        return convertedIntentFilters;
    }

    private InjectionNodeBuilderRepository buildVariableBuilderMap(TypeMirror activityType) {

        InjectionNodeBuilderRepository subRepository = variableBuilderRepositoryFactory.buildRepository(injectionNodeBuilders);

        subRepository.put(Context.class.getName(), contextVariableBuilderProvider.get());
        subRepository.put(Application.class.getName(), applicationVariableBuilderProvider.get());
        subRepository.put(android.app.Activity.class.getName(), contextVariableBuilderProvider.get());
        subRepository.put(Resources.class.getName(), resourcesInjectionNodeBuilderProvider.get());

        //todo: map inheritance of activity type?
        if (activityType != null) {
            subRepository.put(activityType.toString(), contextVariableBuilderProvider.get());
        }

        return subRepository;

    }
}
