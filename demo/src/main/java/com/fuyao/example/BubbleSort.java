package com.fuyao.example;

/**
 * 冒泡排序
 * @author admin
 *
 */
public class BubbleSort {

	public static void bubble(int[] arr){
		if(arr == null || arr.length ==0)return;
		for(int i = 0;i<arr.length-1;i++){
			for(int j = 0;j<arr.length-i-1;j++){
				int temp = arr[j];
				if(temp > arr[j+1]){
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
			}
		}
	}
	public static void main(String[] args) {
		int[] arr = new int[]{3,5,8,1,2,0,4,9,11,10};
		BubbleSort.bubble(arr);
		for(int n : arr){
			System.out.print(n+",");
		}
	}
}
