package com.schoolbridge.v2.domain.academic.schoolAdmin


import java.time.LocalDate

/**
 * Enum representing the possible school administrative role types.
 *
 * These correspond to common administrative positions found in Rwandan schools
 * and can be extended as needed.
 *
 * @property displayName The human-readable name of the role.
 */
enum class SchoolAdminRoleType(val displayName: String) {
    RECTEUR("Recteur"),
    VICE_RECTEUR("Vice-Recteur"),
    PREFET_DISCIPLINE("Préfet de discipline"),
    ANIMATEUR("Animateur"),
    ANIMATRICE("Animatrice"),
    INTENDANT("Intendant"),
    SURVEILLANT("Surveillant"),
    CONSEILLER("Conseiller pédagogique"),
    SECRETAIRE("Secrétaire"),
    CHEF_DE_DEPARTEMENT("Chef de département"),
    RESPONSABLE_INFRASTRUCTURE("Responsable infrastructure")
    // Add more roles as needed
}