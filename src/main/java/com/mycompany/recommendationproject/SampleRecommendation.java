/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.recommendationproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
/**
 *
 * @author Aniejek
 */
@Path("/recommendations")
public class SampleRecommendation {
    
    @POST
    @Path("grades")
    public void addGrade(
            @FormParam("product_id") Integer productID, 
            @FormParam("grade") Integer grade, 
            @FormParam("user_id") Integer userID) throws IOException {
        FileWriter fw = new FileWriter("dataset.csv", true);
        fw.append(userID.toString()+','+productID.toString()+','+grade.toString());
    }    
    
    @GET
    @Path("recommendation")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getRecommendations(
            @QueryParam("user_id") Integer userID,
            @QueryParam("products") Integer products) throws IOException, TasteException{
        DataModel model = new FileDataModel(new File("dataset.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(userID, products);
        List<String> ids = new ArrayList<String>();
        for (RecommendedItem recommendation : recommendations)
        {
            ids.add(Long.toString(recommendation.getItemID()));
        }
        return ids;
    }
}
