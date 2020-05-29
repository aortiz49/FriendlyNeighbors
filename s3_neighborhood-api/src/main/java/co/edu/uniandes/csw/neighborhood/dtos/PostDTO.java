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
//===================================================

import co.edu.uniandes.csw.neighborhood.adapters.DateAdapter;
import co.edu.uniandes.csw.neighborhood.entities.PostEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @k.romero
 */
public class PostDTO implements Serializable{

    /**
     * Represents the date post was made
     */
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date publishDate;
    /**
     * Represents the title of the post
     */
    private String title;

    /**
     * Represents the main text body shown in the post
     */
    private String description;

    /**
     * Represents the number of numberOfLikes of the post
     */
    private Integer numberOfLikes;

    private long id;

    /**
     * Represents the author of this post
     */
    private ResidentProfileDTO author;

    /**
     * Represents the business author of this post
     */
    private BusinessDTO business;

    /**
     * Represents the group this post is shared with
     */
    private GroupDTO group;

    private List<String> album;

    public PostDTO() {
        super();
    }

    public PostDTO(PostEntity entityPost) {

        this.description = entityPost.getDescription();
        this.id = entityPost.getId();
        this.numberOfLikes = entityPost.getNumberOfLikes();
        this.title = entityPost.getTitle();
        this.publishDate = entityPost.getPublishDate();

        if (entityPost.getAuthor() != null) {
            this.author = new ResidentProfileDTO(entityPost.getAuthor());
        }
        if (entityPost.getBusiness() != null) {
            this.business = new BusinessDTO(entityPost.getBusiness());
        }
        if (entityPost.getGroup() != null) {
            this.group = new GroupDTO(entityPost.getGroup());
        }
        album = entityPost.getAlbum();
    }

    /**
     * Converts a post DTO to a post entity.
     *
     * @return new post entity
     *
     */
    public PostEntity toEntity() {
        PostEntity postEntity = new PostEntity();

        postEntity.setDescription(getDescription());
        postEntity.setId(getId());
        postEntity.setNumberOfLikes(getNumberOfLikes());
        postEntity.setTitle(getTitle());
        postEntity.setPublishDate(getPublishDate());

        if (getAuthor() != null) {
            postEntity.setAuthor(getAuthor().toEntity());
        }
        if (getBusiness() != null) {
            postEntity.setBusiness(getBusiness().toEntity());
        }
        if (getGroup() != null) {
            postEntity.setGroup(getGroup().toEntity());
        }
        
          postEntity.setAlbum(getAlbum());

        return postEntity;
    }

    /**
     * @return the publishDate
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * @param publishDate the publishDate to set
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the numberOfLikes
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * @param numberOfLikes the numberOfLikes to set
     */
    public void setNumberOfLikes(Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the author
     */
    public ResidentProfileDTO getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(ResidentProfileDTO author) {
        this.author = author;
    }

    /**
     * @return the business
     */
    public BusinessDTO getBusiness() {
        return business;
    }

    /**
     * @param business the business to set
     */
    public void setBusiness(BusinessDTO business) {
        this.business = business;
    }

    /**
     * @return the group
     */
    public GroupDTO getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(GroupDTO group) {
        this.group = group;
    }

    public List<String> getAlbum() {
        return album;
    }

    public void setAlbum(List<String> album) {
        this.album = album;
    }

}
