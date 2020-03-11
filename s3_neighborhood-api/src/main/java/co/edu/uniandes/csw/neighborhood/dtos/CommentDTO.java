/*
MIT License

Copyright (c) 2020 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.neighborhood.dtos;
//===================================================
// Imports

import co.edu.uniandes.csw.neighborhood.entities.CommentEntity;
import java.io.Serializable;
import java.util.Date;

//===================================================
/**
 * This class represents a comment made in a post
 *
 * @author albayona
 */
public class CommentDTO implements Serializable {

//===================================================
// Attributes
//===================================================
    /**
     * Represents id for this comment
     */
    private Long id;

    /**
     * Represents the date this comment was posted.
     */
    private Date date;

    /**
     * The text body shown in this post.
     */
    private String text;

    /**
     * The author of this comment.
     */
    private ResidentProfileDTO author;

    /**
     * The post the comment belongs to.
     */
    private PostDTO post;

    public CommentDTO() {        
        super();
    }

    /**
     * Creates a comment DTO from a comment entity.
     *
     * @param entityComment entity from which DTO is to be created
     *
     */
    public CommentDTO(CommentEntity entityComment) {
        this.id = entityComment.getId();
        this.date = entityComment.getDate();
        this.text = entityComment.getText();
        
        
        this.post = new PostDTO(entityComment.getPost());
        
        this.author = new ResidentProfileDTO(entityComment.getAuthor());
    }

    /**
     * Converts a comment DTO to a comment entity.
     *
     * @return new comment entity
     *
     */
    public CommentEntity toEntity() {
        CommentEntity commentEntity = new CommentEntity();

        commentEntity.setId(getId());
        commentEntity.setDate(getDate());
        commentEntity.setText(getText());
        if (author != null) {
            commentEntity.setAuthor(author.toEntity());
        }
        if (post != null) {
            commentEntity.setPost(post.toEntity());
        }

        return commentEntity;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param Id the id to set
     */
    public void setId(Long Id) {
        this.id = Id;
    }

}
