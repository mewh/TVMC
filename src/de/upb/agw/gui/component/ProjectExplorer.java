package de.upb.agw.gui.component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.upb.agw.gui.SpotlightGui;
import de.upb.agw.gui.project.DotGraphs;
import de.upb.agw.gui.project.SpotlightFile;
import de.upb.agw.gui.project.SpotlightProject;
import de.upb.agw.gui.tab.Tab;

public class ProjectExplorer extends JTree implements MouseListener, KeyListener {

	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	private SpotlightGui gui;
	private SpotlightProject selectedProject;
		
	public ProjectExplorer(SpotlightGui gui) {
		this.gui = gui;
		
		rootNode = new DefaultMutableTreeNode("projects");
        treeModel = new DefaultTreeModel(rootNode);
        this.setModel(treeModel);
        
        // selection setup
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addMouseListener(this);
        addKeyListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {		
			}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)getLastSelectedPathComponent();	
		
		if(selectedNode == null){			
			return;
		}
		
		if(selectedNode.getUserObject() instanceof String){
			gui.enableRunButton(false);
			gui.enableDeleteButton(false);
			selectedProject=null;
		}
		
		if(selectedNode.getUserObject() instanceof SpotlightProject){
			selectedProject = (SpotlightProject) selectedNode.getUserObject();
			gui.enableRunButton(true);
			gui.enableDeleteButton(true);
		}
		else if(selectedNode.getUserObject() instanceof SpotlightFile || selectedNode.getUserObject() instanceof DotGraphs){
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) selectedNode.getParent();
			selectedProject = (SpotlightProject) tmpNode.getUserObject();
			gui.enableRunButton(true);
			gui.enableDeleteButton(true);
		}
			
		// open file in tab
		if(e.getClickCount() == 2 && selectedNode.getUserObject() instanceof SpotlightFile){				
			gui.openedOrFocusTab((SpotlightFile)selectedNode.getUserObject());
		}
		else if(e.getClickCount() == 2 && selectedNode.getUserObject() instanceof DotGraphs){				
			gui.openedOrFocusTab((DotGraphs)selectedNode.getUserObject());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
	}
	
	 public void insertProjectInTree(SpotlightProject project){
	    	
	 	DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);
	    DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(project.getcFile());
	    DefaultMutableTreeNode ctlNode = new DefaultMutableTreeNode(project.getctlFile());
	    DefaultMutableTreeNode initNode = new DefaultMutableTreeNode(project.getinitFile());
	    DefaultMutableTreeNode dotGraphs = null;
	    
	    // dot graphs
	    if(project.getDotGraphs().size() > 0){
	    	dotGraphs = new DefaultMutableTreeNode(project.getDotGraphs());
	    }
	               
	    treeModel.insertNodeInto(projectNode, rootNode, rootNode.getChildCount());
	    treeModel.insertNodeInto(cNode, projectNode, projectNode.getChildCount());
	    treeModel.insertNodeInto(ctlNode, projectNode, projectNode.getChildCount());
	    treeModel.insertNodeInto(initNode, projectNode, projectNode.getChildCount());
	    
	    if(dotGraphs != null){
	    	treeModel.insertNodeInto(dotGraphs, projectNode, projectNode.getChildCount());
	    }
	    
	    scrollPathToVisible(new TreePath(projectNode.getPath()));
	}
	 
	 public SpotlightProject getSelectedSpotlightProject(){
		 return selectedProject;
	 }

	@Override
	public void keyPressed(KeyEvent e) {
		if(selectedProject != null && e.getKeyCode() == e.VK_DELETE){
			removeSelectedProject();	
		}		
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {		
	}
	
	public void removeSelectedProject(){
		// close all opened tabs from project
		gui.removeTabsFromProject(selectedProject);

		// remove the project from data structure
		gui.removeProject(selectedProject);
		
		System.out.println(selectedProject.toString());
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)getLastSelectedPathComponent();
		
		// select the project
		if(selectedNode.getUserObject() instanceof DotGraphs || selectedNode.getUserObject() instanceof SpotlightFile){
			System.out.println("yepp");
			selectedNode = (DefaultMutableTreeNode)selectedNode.getParent();
		}
			
		// remove subtree from project 
		treeModel.removeNodeFromParent(selectedNode);
			
		// disable the run and delete button
		gui.enableDeleteButton(false);
		gui.enableRunButton(false);	
	}
}