package com.fuyao.example;

/**
 * 二分法查找
 * @author admin
 *
 */
public class DichotomySearch {

	private Integer[] in = new Integer[]{1,3,5,6,8,10};
	
	public static DichotomySearch getInstance(){
		return new DichotomySearch();
	}
	public int search(int n){
		int start = 0 ;
		int end = in.length-1;
		while(start <= end){
			int middle = (start + end)/2;
			if(n < in[middle]){
				end = middle -1 ;
			}else if(n >in[middle]){
				start = middle+1;
			}else{
				return middle;
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
		System.out.println(DichotomySearch.getInstance().search(4));
	}
}
