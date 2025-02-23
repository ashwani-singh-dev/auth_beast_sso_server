package com.zentois.authbeast.enums;

import lombok.Getter;

/**
 * Enum representing different components in the system. (Ncephlon rbac entities)
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Aug-14
 */
@Getter
public enum ComponentName
{
    /**
     * Represents an entity in the system.
     */
    Entity,

    /**
     * Represents an organization in the system.
     */
    Organization,

    /**
     * Represents a staff member in the system.
     */
    Staff,

    /**
     * Represents an account in the system.
     */
    Account,

    /**
     * Represents an organization-account mapping in the system.
     */
    OrganizationAccount,

    /**
     * Represents a staff-account mapping in the system.
     */
    StaffAccount,

    /**
     * Represents an address in the system.
     */
    Address,

    /**
     * Represents a country in the system.
     */
    Country,

    /**
     * Represents a country state mapping in the system.
     */
    CountryStateMap,

    /**
     * Represents a state in the system.
     */
    State,

    /**
     * Represents a city in the system.
     */
    City,

    /**
     * Represents a state city map in the system.
     */
    StateCityMap,

    /**
     * Represents a role in the system.
     */
    Role,

    /**
     * Represents a privilege in the system.
     */
    Privilege,

    /**
     * Represents a role-privilege mapping in the system.
     */
    RolePrivilegeMap,

    /**
     * Represents an entity-role mapping in the system.
     */
    EntityRoleMap,

    /**
     * Represents an entity-role-privilege mapping in the system.
     */
    EntityRolePrivilegeMap,

    /**
     * Represents an product in the thier system.
     */
    Product,

    /**
     * Represents a product-privilege mapping in the system.
     */
    ProductPrivilegeMap,

    /**
     * Represents a user token history in the system.
     */
    AccountTokenHistory,
}