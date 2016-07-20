package com.controller;

import com.dto.JsonVertexList;
import com.dto.Link;
import com.dto.Node;
import com.dto.Vertex;
import com.service.Service;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public ModelAndView test(HttpServletResponse response) throws IOException {
        return new ModelAndView("home");
    }

    @RequestMapping(value = "upload", headers = "content-type=multipart/*", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonVertexList upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        Service service = new Service();
        service.setVertices(new ArrayList<Vertex>());
        InputStream inputStream = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String[] nodes;
        while ((line = reader.readLine()) != null && !line.equals("")) {
            nodes = line.split(" ");
            Vertex firstVertex;
            Vertex secondVertex;
            if (!service.getVertices().contains(new Vertex(nodes[0]))) {
                service.getVertices().add(new Vertex(nodes[0]));
            }
            if (!service.getVertices().contains(new Vertex(nodes[1]))) {
                service.getVertices().add(new Vertex(nodes[1]));
            }
            firstVertex = service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0])));
            secondVertex = service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1])));
            addEdge(firstVertex, secondVertex, service);
        }
        inputStream.close();
        reader.close();

        int community = 1;
        Collections.sort(service.getVertices());
        for (Vertex vertex : service.getVertices()) {
            if (vertex.getCommunity() == null) {
                vertex.setCommunity(getCharForNumber(community));
                for (Vertex neighbor : vertex.getNeighbors()) {
                    if (neighbor.getCommunity() == null) {
                        neighbor.setCommunity(getCharForNumber(community));
                    }
                }
                community++;
            }
        }

        final float permanenceOfGraph = calculatePermanenceForAllVertices(service);
        request.getSession().setAttribute("vertices", service.getVertices());
        return convertToJsonList(service, permanenceOfGraph);

    }

    @RequestMapping(value = "maxpermanence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<JsonVertexList> getMaxPermanence(HttpServletRequest request) {
        Service service = new Service();
        service.setVertices((List<Vertex>) request.getSession().getAttribute("vertices"));
        return maxPermanence(service);
    }

    private boolean eachVertexIsAssignedACommunity(Service service) {
        for (Vertex vertex : service.getVertices()) {
            if (vertex.getCommunity() == null) {
                return false;
            }
        }
        return true;
    }

    private JsonVertexList convertToJsonList(Service service, float permanenceOfGraph) {
        JsonVertexList jsonVertexList = new JsonVertexList();
        DecimalFormat df = new DecimalFormat("#.##");
        jsonVertexList.setPermanenceOfGraph(String.valueOf(df.format(permanenceOfGraph)));
        for (Vertex vertex : service.getVertices()) {
            Node node = new Node();
            node.setName(vertex.getName());
            node.setGroup(vertex.getCommunity());
            node.setPermanence(df.format(vertex.getPermanence()));
            node.setDegree(String.valueOf(vertex.getNeighbors().size()));
            jsonVertexList.getNodes().add(node);
            for (Vertex neighbor : vertex.getNeighbors()) {
                Link link = new Link();
                link.setSource(service.getVertices().indexOf(new Vertex(vertex.getName())));
                link.setTarget(service.getVertices().indexOf(new Vertex(neighbor.getName())));
                jsonVertexList.getLinks().add(link);
            }

        }
        return jsonVertexList;
    }


    private static String getCharForNumber(int i) {
        int count = 0;
        if (i > 0 && i < 27) {
            return String.valueOf((char) (i + 64));
        } else {
            while (!(i > 0 && i < 27)) {
                i = i - 26;
                count++;
            }
            return String.valueOf((char) (count + 64)) +
                    String.valueOf((char) (i + 64));
        }
    }

    private void addEdge(Vertex firstVertex, Vertex secondVertex, Service service) {
        if (service.getVertices().contains(firstVertex) && service.getVertices().contains(secondVertex)) {
            if (!service.getVertices().get(service.getVertices().indexOf(firstVertex)).getNeighbors().contains(service.getVertices().get(service.getVertices().indexOf(secondVertex)))) {
                firstVertex.getNeighbors().add(secondVertex);
                secondVertex.getNeighbors().add(firstVertex);
            }
        }
    }

    private float calculatePermanenceForAllVertices(Service service) {
        float sum = 0;
        for (Vertex vertex : service.getVertices()) {
            service.calculatePermanence(vertex);
            sum = sum + vertex.getPermanence();
        }
        return sum / service.getVertices().size();
    }

    private void recalculatePermanenceWhenEdgeIsAddedOrRemovedFromLocalCommunity(String[] nodes, Service service) {
        if (service.getVertices().contains(new Vertex(nodes[0])) && service.getVertices().contains(new Vertex(nodes[1]))) {
            Vertex v1 = service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0])));
            float oldNumberOfInternalConnections = v1.getNumOfInternalConnections();
            float oldTotalNumberOfConnections = v1.getTotalNumOfConnections();
            float oldClusteringCoefficient = v1.getClusteringCoefficient();
            service.findMaximumNumberOfExternalConnections(v1);
            service.findNumberOfInternalConnections(v1);
            service.findClusteringCoefficient(v1);
            v1.setTotalNumOfConnections(v1.getNeighbors().size());
            float updatedMaximumNumberOfEdgesToExternalCommunity = v1.getMaxNumOfExternalConnections();
            float updatedNumberOfInternalConnections = v1.getNumOfInternalConnections();
            float updatedTotalNumberOfConnections = v1.getTotalNumOfConnections();
            float updatedClusteringCoefficient = v1.getClusteringCoefficient();
            float permanence = (((updatedNumberOfInternalConnections / updatedTotalNumberOfConnections) -
                    (oldNumberOfInternalConnections / oldTotalNumberOfConnections)) / updatedMaximumNumberOfEdgesToExternalCommunity)
                    + (oldClusteringCoefficient - updatedClusteringCoefficient);
            v1.setPermanence(permanence);
        } else {
            System.out.println("One or both vertices do not exist");
        }
    }

    private void deleteEdge(String[] nodes, Service service) throws IOException {
        if (service.getVertices().contains(new Vertex(nodes[0])) && service.getVertices().contains(new Vertex(nodes[1]))) {
            if (service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0]))).getNeighbors().contains(service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1]))))) {
                service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0]))).getNeighbors()
                        .remove(service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1]))));
                service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1]))).getNeighbors()
                        .remove(service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0]))));

            } else {
                System.out.println("Edge does not exist.");
            }
        } else {
            System.out.println("One or both vertices do not exist");
        }
    }

    private void addVertex(String input, Service service) throws IOException {
        String vertexName = input.split(" ")[0];
        String vertexCommunity = input.split(" ")[1];
        Vertex vertex = new Vertex(vertexName);
        vertex.setCommunity(vertexCommunity);
        if (service.getVertices().contains(vertex)) {
            System.out.println("Vertex with name" + vertex.getName() + " already added.");
        } else {
            service.getVertices().add(vertex);
        }
    }

    private void addEdge(String input, Service service) {
        String[] nodes = input.split(" ");
        if (nodes.length > 1 && service.getVertices().contains(new Vertex(nodes[0])) && service.getVertices().contains(new Vertex(nodes[1]))) {
            if (!service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0]))).getNeighbors().contains(service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1]))))) {
                service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0]))).getNeighbors()
                        .add(service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1]))));
                service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[1]))).getNeighbors()
                        .add(service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0]))));
            } else {
                System.out.println("Edge already exists");
            }
        } else {
            System.out.println("Vertex not found");
        }
    }

    private List<JsonVertexList> maxPermanence(Service service) {
        List<JsonVertexList> jsonVertexLists = new ArrayList<>();
        int numOfVertices = service.getVertices().size();
        float sum = 0;
        float oldSum = -1;
        int iteration = 0;
        int maxIteration = 5;
        while (sum != oldSum && iteration < maxIteration) {
            iteration++;
            oldSum = sum;
            sum = 0;
            for (Vertex vertex : service.getVertices()) {
                service.calculatePermanence(vertex);
                float currentPermanence = vertex.getPermanence();
                if (currentPermanence == 1) {
                    sum = sum + currentPermanence;
                } else {
                    float currentNeighborPermanence = 0;
                    for (Vertex neighbor : vertex.getNeighbors()) {
                        service.calculatePermanence(neighbor);
                        currentNeighborPermanence = currentNeighborPermanence + neighbor.getPermanence();
                    }
                    final List<String> communities = getNeighboringCommunities(vertex, service);
                    String bestCommunity = vertex.getCommunity();
                    for (String community : communities) {      //For each neighboring community of V,
                        vertex.setCommunity(community);         //move the vertex into the community
                        service.calculatePermanence(vertex);   //Calculate the permanence when the vertex is in this new community
                        float updatedPermanence = vertex.getPermanence();
                        float updatedNeighborPermanence = 0;
                        for (Vertex neighbor : vertex.getNeighbors()) {
                            service.calculatePermanence(neighbor);     //Calculate new permanence of neighbor
                            updatedNeighborPermanence = updatedNeighborPermanence + neighbor.getPermanence();
                        }
                        if (currentPermanence < updatedPermanence && currentNeighborPermanence < updatedNeighborPermanence) {
                            bestCommunity = vertex.getCommunity();
                            currentPermanence = updatedPermanence;
                        } else {
                            service.calculatePermanence(vertex);
                            for (Vertex neighbor : vertex.getNeighbors()) {
                                service.calculatePermanence(neighbor);
                            }
                            vertex.setCommunity(bestCommunity);
                        }
                    }
                    sum = sum + currentPermanence;
                }
            }
            jsonVertexLists.add(convertToJsonList(service, (sum / numOfVertices)));
        }
        return jsonVertexLists;
    }

    private List<String> getNeighboringCommunities(Vertex vertex, Service service) {
        List<String> communities = new ArrayList<>();
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (!vertex.getCommunity().equals(neighbor.getCommunity())) {
                if(!communities.contains(neighbor.getCommunity())) {
                    communities.add(neighbor.getCommunity());
                }
            }
        }

        return communities;
    }

}
