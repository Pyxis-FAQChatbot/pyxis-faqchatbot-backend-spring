package com.pyxis.backend.store;

import com.pyxis.backend.store.entity.Store;
import com.pyxis.backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    boolean existsByUser(Users user);
}
