package frame;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import dataManager.ActiveCourt;
import dataManager.ActiveUser;
import dataManager.Position;

public class FirstPagePanel extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel mapPanel;
	static JLabel popUpLabel;
	static JLabel innerLabel;
	JLabel userLabel;
	ControlPanel controlPanel;
	//ImageIcon ballIcon = new ImageIcon("res/justABall.png");
	
	
	int x;
	int y;
	Timer timer;
	int timerDelay = 75;
	int raderRadius = 75;
	int animationCount = 0;
	int itemPopCount = 0;

	ArrayList<MapItem> list;
	//MapItem[] itemLabels = new MapItem[10];
	ArrayList<JLabel> itemLabel = new ArrayList<JLabel>();
	

	FirstPagePanel() {
		
		
		
		setBackground(Color.white);
		setLayout(null);
		setSize(600, 900);

		mapPanel = new JPanel();
		mapPanel.setBackground(Color.LIGHT_GRAY);
		mapPanel.setOpaque(false);
		mapPanel.setSize(500, 500);
		mapPanel.setLocation(50, 50);

		ImageIcon myFace = new ImageIcon("res/icons8-human-head-48.png");
		userLabel = new JLabel(myFace);
		// userLabel.setText("me!");
		userLabel.setSize(50, 50);
		userLabel.setOpaque(false);
		userLabel.setLocation(275, 275);

		mapPanel.add(userLabel);

		popUpLabel = new JLabel("  주변 경기장 정보를 탐색중입니다.");
		popUpLabel.setBackground(RootFrame.MAIN_RED);
		popUpLabel.setOpaque(true);
		popUpLabel.setSize(600, 200);
		popUpLabel.setLocation(0, 600);
		
		innerLabel = new JLabel();
		innerLabel.setSize(560, 180);
		innerLabel.setLocation(10, 10);
		innerLabel.setBackground(Color.WHITE);
		innerLabel.setOpaque(true);
		innerLabel.setVisible(false);
		
		popUpLabel.add(innerLabel);

		controlPanel = new ControlPanel();
		controlPanel.setBackground(RootFrame.MAIN_RED);
		controlPanel.setSize(600,100);
		controlPanel.setLocation(0,800);
		
		
		this.add(mapPanel);
		this.add(userLabel);
		this.add(popUpLabel);
		this.add(controlPanel);
		
		//initForTest();
		list = new ArrayList<MapItem>();
		timer = new Timer(250, this);
		timer.start();

	}

	private void getCenterGPS() {
		// 사용자의 현재 위치를 기기로부터 가져오는 method
		Position p = RootFrame.cUser.getCurrentLocation();
		x = Math.round((float)p.getX());
		y = Math.round((float)p.getY());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		// TODO: animate circles
		Graphics2D g2d = (Graphics2D) g;
		raderRadius = 75;
		g2d.setColor(Color.GRAY);
		for (int i = 0; i < 10; i++) {
			raderRadius = raderRadius + (i * 10) + (animationCount * 5); // default : 75 ~ 165
			g2d.drawOval(300 - raderRadius, 300 - raderRadius, 2 * raderRadius, 2 * raderRadius);
			
		}
		animationCount++;
		//System.out.println(animationCount++);
		if(animationCount >= 10) {
			animationCount = 0;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		//System.out.println("Who called action?" + e.getSource());
		getCenterGPS();
		findItems(RootFrame.cUser.getCurrentLocation());
		repaint();
		
	}
	
	public void findItems(Position p) {
		
		// updateRequest(x, y);  
//		for(MapItem item : list) {
//			this.setItem(item);
//		}
		list.removeAll(list);
		
		RootFrame.updateActiveUsers();
		RootFrame.updateActiveCourt();
		
		for (ActiveUser au : RootFrame.aroundAU) {
			list.add(new MapItem(au.getEmail(),
					au.getCurrentLocation().getX(),
					au.getCurrentLocation().getY(),
					MapItem.USER_TYPE));
		}
		
		for (ActiveCourt ac : RootFrame.aroundC) {
			list.add(new MapItem(ac.getId()+"",
					(int)ac.getP().getX(),
					(int)ac.getP().getY(),
					MapItem.COURT_TYPE));
		}
		
		for(int i=0; i < list.size();i++) {
			if(i == itemPopCount) {
				this.setItem(list.get(itemPopCount), itemPopCount);
				itemPopCount++;
				break;
			} else {
				this.updateItem(list.get(i), i);
			}
		}
		
//		if(list.size() > itemPopCount) {
//		 //System.out.println(list.size());
//		  this.setItem(list.get(itemPopCount), itemPopCount);
//		  itemPopCount++; 
//		}
		 
	}
	public void updateItem(MapItem item, int index) {
		if (item.itemType == MapItem.USER_TYPE) {
			itemLabel.get(index).setLocation((int)((item.p.getX() - x) + 300) ,(int)((item.p.getY() - y) + 300));
		} else if (item.itemType == MapItem.COURT_TYPE) {
			itemLabel.get(index).setLocation((int)((item.p.getX() - x) ) ,(int)((item.p.getY() - y) ));
		}
		itemLabel.get(index).revalidate();
		itemLabel.get(index).repaint();
	}
	public void setItem(MapItem item, int index) {
		itemLabel.add(index, item); 
		itemLabel.get(index).setName(""+item.id);
		
		// TODO : implement base on real geometry position
		if (item.itemType == MapItem.USER_TYPE) {
			itemLabel.get(index).setLocation((int)((item.p.getX() - x) + 300) ,(int)((item.p.getY() - y) + 300));
		} else if (item.itemType == MapItem.COURT_TYPE) {
			itemLabel.get(index).setLocation((int)((item.p.getX() - x) ) ,(int)((item.p.getY() - y)));
		}
		
		this.add(itemLabel.get(index));
		//System.out.println("try to set item" + item.id + " at " + (item.p.getX() - x) + "," + (item.p.getY() - y));
	}
	
//	public void initForTest() {
//		list = new ArrayList<MapItem>();
//		list.add(new MapItem(1, 20.0, 20.0, 1));
//		list.add(new MapItem(2, -100.0, -100.0, 1));
//		list.add(new MapItem(3, 200.0, 200.0, 2));
//		list.add(new MapItem(4, -175.0, 175.0, 2));
//	}

	public static void setPopUpLabel(MapItem i) {
		//System.out.println(i.getName());
		//popUpLabel.setText(" !!" + i.id +"를 사용자가 클릭");
		popUpLabel.setText("");
		if(RootFrame.getActiveUser(i.id) != null) {
			innerLabel.setText("<html> " + RootFrame.getActiveUser(i.id).getName() + "님이 주변에 있습니다. <br> 매치를 요청하시겠습니까?</html>");
			innerLabel.setHorizontalAlignment(JLabel.CENTER);
			JButton btnSendMsg = new JButton("요청");
			btnSendMsg.setSize(100,40);
			btnSendMsg.setLocation(230,125);
			btnSendMsg.setFocusable(false);
			btnSendMsg.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					innerLabel.setText("<html> " + RootFrame.getActiveUser(i.id).getName() + "님의 수락을 기다리고 있습니다. <br> </html>");
					getProgressBar();
				}
			});
			innerLabel.add(btnSendMsg);
			
			innerLabel.setVisible(true);
		}
		popUpLabel.repaint();
		
	}
	
	public static void getProgressBar() {
		
		FirstProBar pb = new FirstProBar();
		
		innerLabel.removeAll();
		
		innerLabel.add(pb);
		
	}
}
