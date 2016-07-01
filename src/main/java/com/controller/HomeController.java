package com.controller;

import com.dto.JsonVertexLists;
import com.dto.Link;
import com.dto.Node;
import com.dto.Vertex;
import com.service.Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

	private Service service;
	private MultipartFile multipartFile;

	@Inject
	public HomeController(Service service) {
		this.service = service;
	}

	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException{
		ModelAndView modelAndView = new ModelAndView("home");
		modelAndView.addObject("hello", "world");
		return modelAndView;
	}

	private JsonVertexLists convertToJsonList() {
		JsonVertexLists jsonVertexLists = new JsonVertexLists();
		for (Vertex vertex : service.vertices) {
			Node node = new Node();
			node.setName(vertex.getName());
			node.setGroup(vertex.getCommunity());
			node.setPermanence(vertex.getPermanence());
			jsonVertexLists.getNodes().add(node);
			for (Vertex neighbor : vertex.getNeighbors()) {
				Link link = new Link();
				link.setSource(service.vertices.indexOf(new Vertex(vertex.getName())));
				link.setTarget(service.vertices.indexOf(new Vertex(neighbor.getName())));
				link.setValue(5);
				jsonVertexLists.getLinks().add(link);
			}

		}
		return jsonVertexLists;
	}


	private static String getCharForNumber(int i) {
		return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
	}

	private void addEdge(Vertex firstVertex, Vertex secondVertex) {
		if (service.vertices.contains(firstVertex) && service.vertices.contains(secondVertex)) {
			if (!service.vertices.get(service.vertices.indexOf(firstVertex)).getNeighbors().contains(service.vertices.get(service.vertices.indexOf(secondVertex)))) {
				firstVertex.getNeighbors().add(secondVertex);
				secondVertex.getNeighbors().add(firstVertex);
			} else {
				System.out.println("Edge already exists");
			}
		} else {
			System.out.println("Vertex not found");
		}
	}

	private void getMenu() throws IOException {
		System.out.println("Now what would you like to do now?");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		while (!input.equals("3")) {
			System.out.println("1) Add edge");
			System.out.println("2) Delete edge");
			System.out.println("3) Exit");
			input = bufferedReader.readLine();

			if (input.equals("1")) {

			} else if (input.equals("2")) {
				System.out.println("Enter two vertices to remove the edge between: ");
				input = bufferedReader.readLine();
				String[] nodes = input.split(" ");
				deleteEdge(nodes);
				maxPermanence();
				calculatePermanenceForAllVertices();
				displayPermanenceForAllVertices();
			} else if (input.equals("3")) {
				return;
			} else {
				System.out.println("Please select a value between 1-3");
			}
		}
	}

	private void displayGraphInfo() {
		System.out.print("Vertices:\n");

		for (Vertex vertex : service.vertices) {
			System.out.println("Name: " + vertex.getName());
			System.out.print("Neighbors: ");
			for (Vertex neighbor : vertex.getNeighbors()) {
				System.out.print(neighbor.getName() + " ");
			}
			System.out.println();
			System.out.println("Community: " + vertex.getCommunity());
			System.out.println();
		}
	}

	private void calculatePermanenceForAllVertices() {
		for (Vertex vertex : service.vertices) {
			service.calculatePermanence(vertex);
		}
	}

	private void displayPermanenceForAllVertices() {
		for (Vertex vertex : service.vertices) {
			System.out.println("Permanence for vertex " + vertex.getName() + ": " + vertex.getPermanence());
		}
	}

	private void recalculatePermanenceWhenEdgeIsAddedOrRemovedFromLocalCommunity(String[] nodes) {
		if (service.vertices.contains(new Vertex(nodes[0])) && service.vertices.contains(new Vertex(nodes[1]))) {
			Vertex v1 = service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0])));
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
		if (service.vertices.contains(new Vertex(nodes[0])) && service.vertices.contains(new Vertex(nodes[1]))) {
			if (service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0]))).getNeighbors().contains(service.vertices.get(service.vertices.indexOf(new Vertex(nodes[1]))))) {
				service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0]))).getNeighbors()
						.remove(service.vertices.get(service.vertices.indexOf(new Vertex(nodes[1]))));
				service.vertices.get(service.vertices.indexOf(new Vertex(nodes[1]))).getNeighbors()
						.remove(service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0]))));

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
		if (service.vertices.contains(vertex)) {
			System.out.println("Vertex with name" + vertex.getName() + " already added.");
		} else {
			service.vertices.add(vertex);
		}
	}

	private void addEdge(String input) {
		String[] nodes = input.split(" ");
		if (nodes.length > 1 && service.vertices.contains(new Vertex(nodes[0])) && service.vertices.contains(new Vertex(nodes[1]))) {
			if (!service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0]))).getNeighbors().contains(service.vertices.get(service.vertices.indexOf(new Vertex(nodes[1]))))) {
				service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0]))).getNeighbors()
						.add(service.vertices.get(service.vertices.indexOf(new Vertex(nodes[1]))));
				service.vertices.get(service.vertices.indexOf(new Vertex(nodes[1]))).getNeighbors()
						.add(service.vertices.get(service.vertices.indexOf(new Vertex(nodes[0]))));
			} else {
				System.out.println("Edge already exists");
			}
		} else {
			System.out.println("Vertex not found");
		}
	}

	private float maxPermanence() {
		int numOfVertices = service.vertices.size();
		float sum = 0;
		float oldSum = -1;
		int iteration = 0;
		int maxIteration = 5;
		while (sum != oldSum && iteration < maxIteration) {
			iteration++;
			oldSum = sum;
			sum = 0;
			for (Vertex vertex : service.vertices) {
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
		for (Vertex vertexCom : service.vertices) {
			if (!vertex.getCommunity().equals(vertexCom.getCommunity()) && !communities.contains(vertexCom.getCommunity())) {
				communities.add(vertexCom.getCommunity());
			}
		}
		return communities;
	}

}
