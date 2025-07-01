package com.schoolbridge.v2.domain.user
//
//enum class RelationshipType {
//    MOTHER,
//    FATHER,
//    OLDER_SIBLING,
//    LEGAL_GUARDIAN,
//
//    // ... other types
//}

enum class RelationshipType(val label: String) {
    MOTHER("Mother"),
    FATHER("Father"),
    OLDER_SIBLING("Older sibling"),
    LEGAL_GUARDIAN("Legal guardian"),
    UNCLE("Uncle"),
    AUNT("Aunt"),
    GRANDPARENT("Grandparent"),
    OTHER("Other")
}
