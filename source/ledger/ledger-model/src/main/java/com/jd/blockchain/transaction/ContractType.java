package com.jd.blockchain.transaction;

import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.utils.BaseConstant;

import java.lang.reflect.Method;
import java.util.*;

public class ContractType {

	private String name;

	private SortedMap<String, Method> events = Collections.synchronizedSortedMap(new TreeMap<>());

	private Map<Method, String> handleMethods = new HashMap<>();;

	/**
	 * 返回声明的所有事件；
	 * 
	 * @return
	 */
	public Set<String> getEvents() {
		return events.keySet();
	}

	/**
	 * 返回指定方法声明的事件；<br>
	 * 
	 * 如果不存在，则返回 null；
	 * 
	 * @param method
	 * @return
	 */
	public String getEvent(Method method) {
		return handleMethods.get(method);
	}

	/**
	 * 返回事件的处理方法；<br>
	 * 
	 * 如果不存在，则返回 null；
	 * 
	 * @param event
	 * @return
	 */
	public Method getHandleMethod(String event) {
		return events.get(event);
	}

	private ContractType() {
	}

	public static ContractType resolve(Class<?> contractIntf){
		ContractType contractType = new ContractType();
		//contractIntf contains @Contract and @ContractEvent;
		Method[] classMethods = contractIntf.getDeclaredMethods();
		for (Method method : classMethods) {
			// if current method contains @ContractEvent，then put it in this map;
			if (method.isAnnotationPresent(ContractEvent.class)) {
				Object obj = method.getAnnotation(ContractEvent.class);
				String annoAllName = obj.toString();
				// format:@com.jd.blockchain.contract.model.ContractEvent(name=transfer-asset)
				String eventName_ = obj.toString().substring(BaseConstant.CONTRACT_EVENT_PREFIX.length(),
						annoAllName.length() - 1);
				//if annoMethodMap has contained the eventName, too many same eventNames exists probably, say NO!
				if(contractType.events.containsKey(eventName_)){
					throw new ContractException("too many same eventNames exists in the contract, check it.");
				}
				contractType.events.put(eventName_, method);
				contractType.handleMethods.put(method,eventName_);
			}
		}
		return contractType;
	}

	@Override
	public String toString() {
		return "ContractType{" +
				"name='" + name + '\'' +
				", events=" + events +
				", handleMethods=" + handleMethods +
				'}';
	}
}
