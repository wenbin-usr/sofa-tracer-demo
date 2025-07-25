package edu.whpu.service;

import edu.whpu.entity.Book;
import edu.whpu.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bin_wen
 * @date 2025/1/12 00:33
 */
@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;

    @Transactional
    public void insertBook(Book book) {
        bookMapper.insert(book);
    }

//    @Cacheable(cacheNames = "books")
    public Book getBookById(Long id) {
        return bookMapper.selectById(id);
    }
}
