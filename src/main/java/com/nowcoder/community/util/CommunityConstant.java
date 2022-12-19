package com.nowcoder.community.util;

public interface CommunityConstant {

    /**
     * Activation success
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * Repeated activation
     */
    int REPEATED_ACTIVATION = 1;

    /**
     * Activation failure
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * Default expiration time for login credential
     */
    int DEFAULT_EXPIRATION_SECONDS = 3600 * 12;

    /**
     * Expiration time for login credential if remembered
     */
    int REMEMBERED_EXPIRATION_SECONDS = 3600 * 12 * 100;

    /**
     * Entity type: post
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * Entity type: comment
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * Entity type: user
     */
    int ENTITY_TYPE_USER = 3;
}
