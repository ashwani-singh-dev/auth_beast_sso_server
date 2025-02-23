package com.zentois.authbeast.repository;

import java.util.List;
import java.util.Optional;

import com.zentois.authbeast.model.nceph_entity.Account;

/**
 * Repository for managing {@link Account} entities.
 *
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-July-26
 */
public interface AccountRepository extends GenericRepository<Account, String>
{
        /**
         * Retrieves an {@link Account} by its email.
         *
         * @param email the email of the account
         * @return the account with the given email
         */
        public Account getByEmail(String email);

        /**
         * Retrieves an {@link Account} by its entity ID.
         *
         * @param entityId the entity ID of the account
         * @return the account with the given entity ID
         */
        public List<Account> getByEntityId(String entityId);

        /**
         * Retrieves an {@link Account} by its email.
         *
         * @param email the email of the account
         * @return an {@link Optional} containing the account with the given email, or an empty {@link Optional} if not found
         */
        public Optional<Account> findByEmail(String email);

        /**
         * Checks if an {@link Account} with the given email exists.
         *
         * @param email the email of the account
         * @return {@code true} if an account with the given email exists, {@code false} otherwise
         */
        public boolean existsByEmail(String email);

        /**
         * Checks if an {@link Account} with the given mobile exists.
         *
         * @param mobile the mobile of the account
         * @return {@code true} if an account with the given mobile exists, {@code false} otherwise
         */
        public boolean existsByMobile(String mobile);

        /**
         * Counts the number of {@link Account}s with the given account type and username.
         *
         * @param accountType the account type
         * @param username the username
         * @return the count of accounts with the given account type and username
         */
        public Long countByAccountTypeAndUsername(int accountType, String username);
        
        /**
         * Account according to account type and email 
         * 
         * @param accountType
         * @param email
         * @return Account
         */
        public Account findByAccountTypeAndEmail(Integer accountType, String email);
}