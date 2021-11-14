package m.sort.mach;

public class MachSort {
    private static final int THRESHOLD = 15;
    public static void main(String[] args) {
    }

    /**
     * Sorts the specified array of ints into ascending order.
     *
     * @param a the array to be sorted
     */
    public static int[] machSort(int[] a) {
        int length = a.length;
        if( length <= THRESHOLD){
            return insertionSort(a);
        }else{
            if( length % THRESHOLD == 0){
                int numOfSubArrays = length / THRESHOLD;
                a = machSort(a, numOfSubArrays);
            }else{
                //do subarrays of size THRESHOLD, but the last one that will be of size less than threshold we do a small insertion sort.
                //Or maybe we just do via threshold and then return the array. So we call the machsort above and do subarays of size threshold.
                //But the returned array will be partially ordered still and we don;t care about those last (n) elements. as the final insertion sort will take care of them.
            }
        } 
        return a;
    }

    /**
     * Sorts the specified array of ints by subarrays to get a partially ordered array.
     * This is to be used on arrays of length % 15 == 0.
     * @param a the array to be sorted
     * @param numOfSubArrays the number of subarrays to be sorted
     */
    public static int[] machSort(int[] a, int numOfSubArrays){
        int low = 0;
        int high = THRESHOLD;
        for(int i = 0; i < numOfSubArrays; i++){
            a = machSort(a, low, high);
            low = high;
            high += THRESHOLD;
            if( high > a.length){
                return a;
            }
        }
        return a;


    }

    /**
     * This method does an insertion sort on the subarray of a given array. This allowa for fast sorting of subarrays
     * which will get a partially sorted array that is then sorted by the insertion sort. Insertion sort is fast on partially ordered arrays.
     * @param a - the array to be sorted
     * @param low - the low index of the subarray
     * @param high - the high index of the subarray
     * @return
     */
    public static int[] machSort(int[] a, int low, int high){
        for(int i = low; i < high; i++){
            for(int j = i; j < high; j++){
                if(a[i] > a[j]){
                    int temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        return a;
    }

    public static int[] insertionSort(int[] a){
        int length = a.length;
        for(int i = 1; i < length; i++){
            int j = i;
            while(j > 0 && a[j] < a[j-1]){
                swap(a, j, j-1);
                j--;
            }
        }
        return a;
    }
    public static void swap (int[] a, int i, int j){
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}