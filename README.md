# Practical-Data-Structures-and-Algorithms-113-1-BIME
113-2 NTU BIME Class


# 作業題目網址:  
hw01(Union): https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/SyieYHDP1l  
hw02(動態規劃 DP): https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/B1BlzSwFJe  
hw03(單調棧 Monotonic Stack): https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/S1edebLt5Jx  
Supplementary 03_1: 分別用 Array 與 Linked List 實作 Stack, Queue. 矩陣實作的版本需要可以動態增減.  
Supplementary 03_2: 循環鏈表, 雙向鏈表.  
hw04(凸殼 Convex Hull): https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/HkzhEbmskl  
hw05: https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/BJ7EdE2jye  
Supplementary 05_1: 實作 Knuth Shuffle, Bubble Sort, Selection Sort, Insertion Sort, Shell Sort (分治的 Insert sort).  
Supplementary 05_2: 實作 Merge Sort, Quick Sort (Median of 3), 3 Way Quick Sort.  
Supplementary 05_3: 實作 Heap Sort.  
hw06(Event Queue): https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/r1jOYE2iye  
hw07(質心分群 centroid hierarchical clustering algorithm): https://hackmd.io/@CiqLOooyRwWmK--mMkfetA/HkHS8UcC1x  
Supplementary 07_1: 實作 紅黑樹  

## 筆記
考慮用哪種 sort 應該要考慮  
1. 穩定性:在遇到相同大小時不能交換，並且不可破壞上次排序的結果。通常只要不是鄰近兩兩交換的，都不穩定。(只有merge, insert sort是穩定)  
2. 確定性:有一致的執行過程。快速排序因為一開始會洗牌，所以沒有確定性。  
3. 平行度:快速排序無法平行化  
4. 空間花費:merge sort需要額外空間保存分治過程，無法直接交換。  
![alt text](image.png)  

Symbol Table 實作方法:  
1. linklist  
2. 雙陣列分別儲存 key, value。 放入時要排序，可以使用 binary search。  
3. Binary Search Tree (Hibbard Deletion)  
   ```
        10
       /  \
      5    20
          /  \
        15    30
   ```
	- case1. 刪除節點在最下面: 把 parent 的 link 設為 nullptr   
	- case2. 刪除節點的兩根腳只有一個有連接(另一個為空): 直接把 parent 的 link 連接到子節點  
	- case3. 刪除節點的兩根腳都有值: 找左邊的最大(往左走一步後往右走到底)或是右邊的最小(往右走一步後往左走到底)，來取代要被刪除的位置。  
  			具體方式為先把原本被取代(10)和要取代的(15)都先複製起來，把複製的要取代節點(15)兩腳都設為與被取代的相同。刪掉原本要取代的節點(15)(因為一定在最底部，所以很好刪)。  
			最後回傳新增的複製節點位置(15)，再手動把父節點或根節點的位置重新接到回傳值。被刪除的節點，因為沒人指到他會自己被回收。  
4. 紅黑樹: https://www.bilibili.com/video/BV1piF6erE7Y?spm_id_from=333.788.videopod.sections&vd_source=a7ec40e3a00a70629191f9c178661833  

## 幾何搜尋
113-2-PDSA-20250502-h2  
1. KD-tree: 應用於 range search & nearest neighbor,  
2. interval search tree
![alt text](image-1.png)


# 作業繳交網址:
https://c4lab.bime.ntu.edu.tw:13000/


# 課本提供函式庫:  
https://algs4.cs.princeton.edu/code/  
https://github.com/kevin-wayne/algs4/tree/master/src/main/java/edu/princeton/cs/algs4

