package com.controller;

import com.dto.JsonVertexLists;
import com.dto.Link;
import com.dto.Node;
import com.dto.Vertex;
import com.service.Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class HomeController {

	private Service service;

	@Inject
	public HomeController(Service service) {
		this.service = service;
	}

	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException{
		return new ModelAndView("home");
	}

	@RequestMapping(value="upload", headers = "content-type=multipart/*", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public JsonVertexLists upload(@RequestParam("file") MultipartFile file) throws IOException {
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
			addEdge(firstVertex, secondVertex);
		}
		inputStream.close();
		reader.close();
		int index = 1;

		final Iterator<Vertex> iterator = service.getVertices().iterator();
		Vertex previous = iterator.next();
		previous.setCommunity(getCharForNumber(index));
		Vertex next;
		while (iterator.hasNext()) {
			next = iterator.next();

			if(next.getNeighbors().contains(previous)){
				next.setCommunity(getCharForNumber(index));
			}else{
				index++;
				next.setCommunity(getCharForNumber(index));
			}

			previous = next;
		}

		calculatePermanenceForAllVertices();
		maxPermanence();
		calculatePermanenceForAllVertices();

		return convertToJsonList();

	}

	private JsonVertexLists convertToJsonList() {
		JsonVertexLists jsonVertexLists = new JsonVertexLists();
		for (Vertex vertex : service.getVertices()) {
			Node node = new Node();
			node.setName(vertex.getName());
			node.setGroup(vertex.getCommunity());
			node.setPermanence(vertex.getPermanence());
			jsonVertexLists.getNodes().add(node);
			for (Vertex neighbor : vertex.getNeighbors()) {
				Link link = new Link();
				link.setSource(service.getVertices().indexOf(new Vertex(vertex.getName())));
				link.setTarget(service.getVertices().indexOf(new Vertex(neighbor.getName())));
				link.setValue(5);
				jsonVertexLists.getLinks().add(link);
			}

		}
		return jsonVertexLists;
	}


	private static String getCharForNumber(int i) {
		int count = 0;
		if(i > 0 && i < 27){
			return String.valueOf((char)(i + 64));
		}else{
			while(!(i > 0 && i < 27)){
				i = i - 26;
				count++;
			}
			return String.valueOf((char) (count + 64)) +
					String.valueOf((char) (i + 64));
		}
	}

	private void addEdge(Vertex firstVertex, Vertex secondVertex) {
		if (service.getVertices().contains(firstVertex) && service.getVertices().contains(secondVertex)) {
			if (!service.getVertices().get(service.getVertices().indexOf(firstVertex)).getNeighbors().contains(service.getVertices().get(service.getVertices().indexOf(secondVertex)))) {
				firstVertex.getNeighbors().add(secondVertex);
				secondVertex.getNeighbors().add(firstVertex);
			}
		}
	}

	private void calculatePermanenceForAllVertices() {
		for (Vertex vertex : service.getVertices()) {
			service.calculatePermanence(vertex);
		}
	}

	private void recalculatePermanenceWhenEdgeIsAddedOrRemovedFromLocalCommunity(String[] nodes) {
		if (service.getVertices().contains(new Vertex(nodes[0])) && service.getVertices().contains(new Vertex(nodes[1]))) {
			Vertex v1 = service.getVertices().get(service.getVertices().indexOf(new Vertex(nodes[0])));
			float oldNumberOfInternalConnections = v1.getNumOfInternalConnections();
			float oldTotalNumberOfConnections = v1.getTotalNumOfConnections();
			float oldClusteringCoefficient = v1.getClusteringCoefficient();
			service.getMaximumNumberOfExternalConnections(v1);
			service.getNumberOfInternalConnections(v1);
			service.getClusteringCoefficient(v1);
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

	private void deleteEdge(String[] nodes) throws IOException {
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

	private void addVertex(String input) throws IOException {
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

	private void addEdge(String input) {
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

	private float maxPermanence() {
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
					final List<String> communities = getNeighboringCommunities(vertex);
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
							vertex.setCommunity(bestCommunity);
						}
					}
					sum = sum + currentPermanence;
				}
			}
		}
		return (sum / numOfVertices); //Return the permanence of the graph
	}

	private List<String> getNeighboringCommunities(Vertex vertex) {
		List<String> communities = new ArrayList<>();
		for (Vertex vertexCom : service.getVertices()) {
			if (!vertex.getCommunity().equals(vertexCom.getCommunity()) && !communities.contains(vertexCom.getCommunity())) {
				communities.add(vertexCom.getCommunity());
			}
		}
		return communities;
	}

}
