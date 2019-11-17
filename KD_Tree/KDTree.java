//package assignments2019.a3posted;
//Name: Jinyue Jiang 
//ID: 260711283
import java.util.ArrayList;
import java.util.Iterator;
public class KDTree implements Iterable<Datum>{ 

	KDNode 		rootNode;
	int    		k; //dimension
	int			numLeaves;
	
	// constructor

	public KDTree(ArrayList<Datum> datalist) throws Exception {

		Datum[]  dataListArray  = new Datum[ datalist.size() ]; 

		if (datalist.size() == 0) {
			throw new Exception("Trying to create a KD tree with no data");
		}
		else
			this.k = datalist.get(0).x.length;

		int ct=0;
		for (Datum d :  datalist) {
			dataListArray[ct] = datalist.get(ct);
			ct++;
		}
		
	//   Construct a KDNode that is the root node of the KDTree.

		rootNode = new KDNode(dataListArray);
	}
	
	//   KDTree methods
	
	public Datum nearestPoint(Datum queryPoint) {
		return rootNode.nearestPointInNode(queryPoint);
	}
	

	public int height() {
		return this.rootNode.height();	
	}

	public int countNodes() {
		return this.rootNode.countNodes();	
	}
	
	public int size() {
		return this.numLeaves;	
	}

	//-------------------  helper methods for KDTree   ------------------------------

	public static long distSquared(Datum d1, Datum d2) {

		long result = 0;
		for (int dim = 0; dim < d1.x.length; dim++) {
			result +=  (d1.x[dim] - d2.x[dim])*((long) (d1.x[dim] - d2.x[dim]));
		}
		// if the Datum coordinate values are large then we can easily exceed the limit of 'int'.
		return result;
	}

	public double meanDepth(){
		int[] sumdepths_numLeaves =  this.rootNode.sumDepths_numLeaves();
		return 1.0 * sumdepths_numLeaves[0] / sumdepths_numLeaves[1];
	}
	
	class KDNode { 

		boolean leaf;
		Datum leafDatum;           //  only stores Datum if this is a leaf
		
		//  the next two variables are only defined if node is not a leaf

		int splitDim;      // the dimension we will split on
		int splitValue;    // datum is in low if value in splitDim <= splitValue, and high if value in splitDim > splitValue  

		KDNode lowChild, highChild;   //  the low and high child of a particular node (null if leaf)
		  //  You may think of them as "left" and "right" instead of "low" and "high", respectively

		KDNode(Datum[] datalist) throws Exception{
	
			/*
			 *  This method takes in an array of Datum and returns 
			 *  the calling KDNode object as the root of a sub-tree containing  
			 *  the above fields.
			 */
			double range=0;//for collecting range of the dimension.
			double sum=0;
			int same=0;
			for (int i = 0; i < datalist.length&&same==0; i++) {//check if a datalist has all datum are the same.
					if (!datalist[0].equals(datalist[i])) same++;
			}
			
			if (datalist.length==1||same==0) {//if it is leaf Datum, then return 
				leaf=true;
				numLeaves++;
				leafDatum=datalist[0];
				lowChild=null;
				highChild=null;
				return;
			}
			
			//1.determine the splitDim and splitValue
			for (int i = 0; i < datalist[0].x.length; i++) {//determine the dimension of Datum
				int max=datalist[0].x[i];
				int min=datalist[0].x[i];//every time with a new dimension has new max and min.
				
				for (int j = 1; j < datalist.length; j++) {//find the max of ith dimension.
					
					//if(datalist[j].x[i]>max) max=datalist[j].x[i];
					//if(datalist[j].x[i]<min) min=datalist[j].x[i];			
					max = Math.max(max, datalist[j].x[i]);
					min = Math.min(min, datalist[j].x[i]);
				}
				if ((max-min)>range) {//take the first large dimension.
					range= max-min;
					sum=max+min;
					splitDim=i;//only iterating the ith order of the dimension.
				}
			}
			splitValue=(int)sum/2;
			double mid=sum/2;
			//2. split the array into two. use mid instead of splitValue.
			int lowSize=0;
			int highSize=0;
			for (int i = 0; i < datalist.length; i++) {
				if (datalist[i].x[splitDim]<=mid) {
					lowSize++;
				}else highSize++;
			}	
			Datum[] low=new Datum[lowSize];//the longest length 
			Datum[] high=new Datum[highSize];//the longest length 
			//need to figure out the length of the datum, if and /2?
			int j=0;
			int q=0;//count
			for (int i = 0; i < datalist.length; i++) {
				if (datalist[i].x[splitDim]<=mid) {
				low[j]=datalist[i];
				j++;
				}else {
					high[q]=datalist[i];
					q++;
				}
			}
			this.lowChild=new KDNode(low);
			this.highChild=new KDNode(high);
			leaf=false; 	
		}

		public Datum nearestPointInNode(Datum qP) {
			Datum R1, R2, R3,R4,qPnew;
		if (!this.leaf) {
			if (qP.x[this.splitDim]<=this.splitValue) {//if the query on the left of the splitValue
				R1=this.lowChild.nearestPointInNode(qP);//start again with lowChild
				//qPnew=qP;
				//qPnew.x[this.splitDim]=this.splitValue;
				//if(Math.pow(R1.x[this.splitDim]-qP.x[this.splitDim], 2)<Math.pow(qP.x[this.splitDim]-this.splitValue, 2)) return R1;//compare the distance and see if has a change to get smaller point
				if(distSquared(R1, qP)<Math.pow(qP.x[this.splitDim]-this.splitValue, 2)) return R1;//compare the distance and see if has a change to get smaller point
				else {
					R2=this.highChild.nearestPointInNode(qP);//check the point comes from the otherside.
					if(distSquared(R1, qP)<distSquared(R2, qP))return R1;
					else return R2;
				}
			}else {
				R3=this.highChild.nearestPointInNode(qP);//logic the same as above
//				if(Math.pow(R3.x[this.splitDim]-qP.x[this.splitDim], 2)<Math.pow(qP.x[this.splitDim]-this.splitValue, 2)) return R3;
				if(distSquared(R3, qP)<Math.pow(qP.x[this.splitDim]-this.splitValue, 2)) return R3;
				else {
					R4=this.lowChild.nearestPointInNode(qP);
					if(distSquared(R3, qP)<distSquared(R4, qP))return R3;
					else return R4;
				}
			}
		}else return leafDatum;
		}	

		public Datum closerDatum(Datum nearestPoint,Datum otherSideNearestPoint,Datum queryPoint){
			if(distSquared(nearestPoint,queryPoint)<distSquared(otherSideNearestPoint,queryPoint))
			{
				return  nearestPoint;
			}
			return otherSideNearestPoint;
			
		}
		
		// -----------------  KDNode helper methods (might be useful for debugging) -------------------

		public int height() {
			if (this.leaf) 	
				return 0;
			else {
				return 1 + Math.max( this.lowChild.height(), this.highChild.height());
			}
		}

		public int countNodes() {
			if (this.leaf)
				return 1;
			else
				return 1 + this.lowChild.countNodes() + this.highChild.countNodes();
		}
		
		/*  
		 * Returns a 2D array of ints.  The first element is the sum of the depths of leaves
		 * of the subtree rooted at this KDNode.   The second element is the number of leaves
		 * this subtree.    Hence,  I call the variables  sumDepth_size_*  where sumDepth refers
		 * to element 0 and size refers to element 1.
		 */
				
		public int[] sumDepths_numLeaves(){
			int[] sumDepths_numLeaves_low, sumDepths_numLeaves_high;
			int[] return_sumDepths_numLeaves = new int[2];
			
			/*     
			 *  The sum of the depths of the leaves is the sum of the depth of the leaves of the subtrees, 
			 *  plus the number of leaves (size) since each leaf defines a path and the depth of each leaf 
			 *  is one greater than the depth of each leaf in the subtree.
			 */
			
			if (this.leaf) {  // base case
				return_sumDepths_numLeaves[0] = 0;
				return_sumDepths_numLeaves[1] = 1;
			}
			else {
				sumDepths_numLeaves_low  = this.lowChild.sumDepths_numLeaves();
				sumDepths_numLeaves_high = this.highChild.sumDepths_numLeaves();
				return_sumDepths_numLeaves[0] = sumDepths_numLeaves_low[0] + sumDepths_numLeaves_high[0] + sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
				return_sumDepths_numLeaves[1] = sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
			}	
			return return_sumDepths_numLeaves;
		}
		
	}

	public Iterator<Datum> iterator() {
		return new KDTreeIterator();
	}
	
	private class KDTreeIterator implements Iterator<Datum> {

		ArrayList<Datum> datumArray;
		int size;
		int index = 0;

		public KDTreeIterator() {
			datumArray = new ArrayList<>();
			inOrder(rootNode);
			size = datumArray.size();
		}
		//inOrder traversing method of KDNode
		public void inOrder(KDNode node) {
			if (node.lowChild != null) 	inOrder(node.lowChild);
			if (node.leaf) 	datumArray.add(node.leafDatum);
			if (node.highChild != null)  inOrder(node.highChild);
		}
		@Override
		public boolean hasNext() {
			return size > index;
		}
		@Override
		public Datum next() {
			return datumArray.get(index++);
		}
	}

	}



