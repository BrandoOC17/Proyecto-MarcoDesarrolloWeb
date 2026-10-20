package com.infinitystation.MarcosDesarrolloWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infinitystation.MarcosDesarrolloWeb.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
}

