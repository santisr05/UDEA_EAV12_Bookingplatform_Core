package com.codefactory.bookingplatform.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final String BASE = "com.codefactory.bookingplatform";
    private final com.tngtech.archunit.core.domain.JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    @Test
    void domain_layer_never_depends_on_spring_or_jakarta_persistence() {
        ArchRule rule = noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta.persistence..");
        rule.check(classes);
    }

    @Test
    void domain_never_depends_on_infrastructure_or_api() {
        ArchRule rule = noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..api..");
        rule.check(classes);
    }

    @Test
    void identity_only_uses_auth_application_facade() {
        ArchRule rule = noClasses().that().resideInAPackage(BASE + ".identity..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE + ".auth.infrastructure..", BASE + ".auth.api..");
        rule.check(classes);
    }

    @Test
    void auth_never_depends_on_identity() {
        ArchRule rule = noClasses().that().resideInAPackage(BASE + ".auth..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".identity..");
        rule.check(classes);
    }

    @Test
    void catalog_domain_never_depends_on_scheduling_or_booking() {
        ArchRule rule = noClasses().that().resideInAPackage(BASE + ".catalog.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE + ".scheduling..", BASE + ".booking..");
        rule.check(classes);
    }

    @Test
    void scheduling_domain_never_depends_on_booking() {
        ArchRule rule = noClasses().that().resideInAPackage(BASE + ".scheduling.domain..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".booking..");
        rule.check(classes);
    }

    @Test
    void identity_domain_never_depends_on_booking_scheduling_or_catalog() {
        ArchRule rule = noClasses().that().resideInAPackage(BASE + ".identity.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE + ".booking..", BASE + ".scheduling..", BASE + ".catalog..");
        rule.check(classes);
    }

    @Test
    void use_cases_are_named_consistently() {
        ArchRule rule = classes().that().resideInAPackage("..application..")
                .and().haveSimpleNameEndingWith("UseCase")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class);
        rule.check(classes);
    }
}
