/*
 * �߳��࣬���ڷ�������ͻ��˵ĳ�����ͨ
 */

package com.qq.server.tools;

import java.net.*;
import java.util.*;
import java.io.*;

import com.qq.common.*;


public class ServerConnectClientThread extends Thread{
	Socket s;
	
	public ServerConnectClientThread(Socket s){
		//�ѷ�������ÿͻ��˵����Ӹ���s
		this.s = s;
	}
	
	//�ڸ��߳���֪ͨ�����û�
	public void NoticeOther(String iam){
		//�õ��������ߵ��߳�
		HashMap hm = ManageServerConClientThread.hm;
		Iterator it = hm.keySet().iterator();
		
		while(it.hasNext()){
			//ȡ�������˵�QQ��
			Message ms = new Message();
			ms.setCon(iam);//˵�������ǣ�������
			ms.setMesType(MessageType.message_receive_OnlineFriend);
			String OnlineUserId = it.next().toString();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(ManageServerConClientThread.getClientThread(OnlineUserId).s.getOutputStream());
				ms.setGetter(OnlineUserId);//������˵
				oos.writeObject(ms);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void run(){
		while(true){
			
			try {
				//������տͻ��˷�������Ϣ
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				Message ms = (Message)ois.readObject();
				
				if(ms.getMesType().equals(MessageType.message_common_message)){
					System.out.println(ms.getSender()+"��"+ms.getGetter()+"˵"+ms.getCon());

					//����Ϣת�������Ե��õ�Ҫת����getter��socket
					ServerConnectClientThread sr = ManageServerConClientThread.getClientThread(ms.getGetter());
					ObjectOutputStream oos = new ObjectOutputStream(sr.s.getOutputStream());//ע�������д���õ���getter��socket
					oos.writeObject(ms);
				}else if(ms.getMesType().equals(MessageType.message_get_OnlineFriend)){
					//���ڷ������ĺ��ѷ���
					String res = ManageServerConClientThread.GetOnlineUserId();
					Message ms_User = new Message();
					ms_User.setMesType(MessageType.message_receive_OnlineFriend);
					ms_User.setCon(res);
					ms_User.setGetter(ms.getSender());
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					oos.writeObject(ms_User);
					
				}
				

				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}