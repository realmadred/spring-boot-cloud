package com.rxjava.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserService {

    private static final ConcurrentMap<String,String> repository = new ConcurrentHashMap<>();
    private static final List<String> userIds = Arrays.asList("a","b","c","d","e","f","g");

    static {
        userIds.forEach(s -> repository.put(s,"hello"+s));
    }

    public static List<String> getUserIds() {
        return userIds;
    }

    public static String getUser(String s) {
        return repository.get(s);
    }
}
