package com.si.jupiter.smart.pools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-11 下午2:09
 * Last Update:  17-5-11 下午2:09
 */
public interface Hashing {
    Hashing MURMUR_HASH = new MurmurHash();
    ThreadLocal<MessageDigest> md5Holder = new ThreadLocal();
    Hashing MD5 = new Hashing() {
        public long hash(String key) {
            return this.hash(SafeEncoder.encode(key));
        }

        public long hash(byte[] key) {
            try {
                if(md5Holder.get() == null) {
                    md5Holder.set(MessageDigest.getInstance("MD5"));
                }
            } catch (NoSuchAlgorithmException var6) {
                throw new IllegalStateException("++++ no md5 algorythm found");
            }

            MessageDigest md5 = (MessageDigest)md5Holder.get();
            md5.reset();
            md5.update(key);
            byte[] bKey = md5.digest();
            long res = (long)(bKey[3] & 255) << 24 | (long)(bKey[2] & 255) << 16 | (long)(bKey[1] & 255) << 8 | (long)(bKey[0] & 255);
            return res;
        }
    };

    long hash(String var1);

    long hash(byte[] var1);
}
