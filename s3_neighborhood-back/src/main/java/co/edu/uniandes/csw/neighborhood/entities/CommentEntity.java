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
package co.edu.uniandes.csw.neighborhood.entities;
//===================================================
// Imports
//===================================================

import co.edu.uniandes.csw.neighborhood.podam.DateStrategy;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStrategyValue;

/**
 * This class represents a comment made in a post
 *
 * @author albayona
 */
@Entity
public class CommentEntity extends BaseEntity implements Serializable {

//===================================================
// Relations
//===================================================
    /**
     * The author of this comment.
     */
    @PodamExclude
    @ManyToOne
    private ResidentProfileEntity author;

    /**
     * The post the comment belongs to.
     */

    @PodamExclude
    @ManyToOne
    private PostEntity post;
    

//===================================================
// Attributes
//===================================================
    /**
     * Represents the date this comment was posted.
     */
    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date date;

    /**
     * The text body shown in this post.
     */
    private String text;

//===================================================
// Getters & Setters
//===================================================

    public ResidentProfileEntity getAuthor() {
        return author;
    }

    public PostEntity getPost() {
        return post;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public void setAuthor(ResidentProfileEntity author) {
        this.author = author;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    

}
