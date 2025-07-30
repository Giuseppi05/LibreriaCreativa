package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Category;
import com.LibreriaCreativa.LibreriaCreativa.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoriaRepository;

    public Page<Category> obtenerCategoriasPaginadas(int page, int size) {
        return categoriaRepository.findAll(PageRequest.of(page, size));
    }
    
    public Optional<Category> buscarPorId(Integer id){
        return categoriaRepository.findById(id);
    }
    
    public List<Category> listarTodas(){
        return categoriaRepository.findAll();
    }
    
    public void borrarPorId(Integer id){
        categoriaRepository.deleteById(id);
    }
    
    public Optional<Category> buscarPorNombre(String name){
        return categoriaRepository.findByName(name);
    }
    
    public Category guardar(Category c){
        return categoriaRepository.save(c);
    }
}
