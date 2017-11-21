import java.util.Scanner;

/**
 * class Board sebagai class utama permainan Congklak
 */
public class Board {

    private Holes gameholes;
    private String currentPlayer;
    private boolean justStart;

    public static void main(String args[]) {
        Board board = new Board();
        board.tampilkanKeterangan();
        board.mainkan();
        board.tampilkanNilaiAkhir();
    }


    public Board() {
        gameholes = new Holes();
    }


    /**
     * Method untuk menampilkan nilai akhir ketika permainan sudah selesai
     */
    public void tampilkanNilaiAkhir() {
        int nilaiA = gameholes.getHoleVal("A",7);
        int nilaiB = gameholes.getHoleVal("B",7);
        String greet = "Pemain ";
        greet+= (nilaiA > nilaiB) ? "A" : "B";
        greet+= " menang!";
        System.out.println("Permainan berakhir!!");
        System.out.println(greet);
    }


    /**
     * Method untuk menampilkan keterangan awal saat permainan akan dimulai
     */
    public void tampilkanKeterangan(){

        System.out.println("==============================");
        System.out.println("Game Congklak Sederhana");
        System.out.println("==============================");
        System.out.println("Pilih Rumah sesuai nomornya!");
        System.out.println("==============================");
        System.out.println("Peta:");
        System.out.println("R 6 5 4 3 2 1 0 ===>Pemain B");
        tampilkanHoles();
        System.out.println("0 1 2 3 4 5 6 R ===>Pemain A");
        System.out.println("Keterangan:");
        System.out.println("R = Lobang utama yang akan menjadi poin masing-masing pemain");
        System.out.println("==============================");
        System.out.println("Mulai permainan!");
    }


    /**
     * Method untuk memainkan mechanics dari permainan
     */
    public void mainkan() {
        tampilkanHoles();
        currentPlayer = "B";
        while(!isStop()) {
            changePlayer();
            chooseHome();

            while(justStart || !gameholes.isStop()) {
                tampilkanHoles();
                justStart = false;
                int round = gameholes.pickHole();

                for(int i=0;i<round;i++) {
                    gameholes.addNext(currentPlayer);
                }
                gameholes.decrementCurrentHole();
                int currentHole = gameholes.currentHole;

                if( gameholes.canChoose(currentPlayer) && ((currentHole==7 || currentHole==15)) ) {
                    tampilkanHoles();
                    System.out.println(currentHole);
                    chooseHome();
                }

            }
            if(gameholes.canShoot(currentPlayer)) {
                gameholes.shoot(currentPlayer);
            }
            tampilkanHoles();
        }
    }


    /**
     * Method perbantuan untuk memilih rumah awal pengambilan biji congklak
     * dari pemain yang memulai/meneruskan permainan
     */
    private void chooseHome() {
        //pilih rumah sesuai pemain
        Scanner in = new Scanner(System.in);
        int index = 0;
        do {
            System.out.print("Pilih rumah pemain "+currentPlayer+": ");
            index = in.nextInt();
        } while(index>6 || gameholes.getHoleVal(currentPlayer,index)==0);

        gameholes.setHole(currentPlayer,index);
        justStart = true;
    }


    /**
     * Method perbantuan untuk mengganti pemain ketika ronde pemain lain berakhir
     */
    private void changePlayer() {
        switch (currentPlayer) {
            case "A": currentPlayer="B";break;
            case "B": currentPlayer="A";break;
        }
    }


    /**
     * Method perbantuan untuk melihat apakah permainan sudah dapat diselesaikan
     * Permainan selesai ketika semua biji telah berada pada lobang utama
     * @return nilai boolean apakah permainan sudah selesai atau belum
     */
    private boolean isStop() {
        int[] holesA = gameholes.getHoleA();
        int[] holesB = gameholes.getHoleB();
        int sum = 0;
        for(int i=0; i < holesA.length-1; i++) {
            sum+=holesA[i];
        }

        for(int i=0; i < holesB.length-1; i++) {
            sum+=holesB[i];
        }

        return sum==0;
    }

    /**
     * Method perbantuan untuk menampilkan kondisi dari board
     */
    private void tampilkanHoles() {
        int[] holesA = gameholes.getHoleA();
        int[] holesB = gameholes.getHoleB();
        System.out.println("=================");
        for(int i=holesB.length-1; i >= 0; i--) {
            System.out.print(holesB[i] + " ");
        }
        System.out.println();
        for(int i=0; i < holesA.length; i++) {
            System.out.print(holesA[i] + " ");
        }
        System.out.println();
        System.out.println("=================");
    }


    /**
     * Inner Class yang merepresentasikan lobang dan mechanicsnya
     * pada permainan dakon
     */
    private class Holes {
        private int[] holeA, holeB;
        private static final int HOLE_NUM = 8;
        private int currentHole;

        /**
         * Pada saat awal permainan, setiap lobang diisikan dengan nilai 7
         */
        Holes() {
            holeA = new int[HOLE_NUM];
            holeB = new int[HOLE_NUM];
            for(int i=0; i < HOLE_NUM-1; i++) {
                holeA[i] = HOLE_NUM-1;
                holeB[i] = HOLE_NUM-1;
            }
        }


        /**
         * Method asesor untuk mendapatkan isi dari lobang
         * pada region pemain A
         * @return nilai-nilai pada setiap lobang pemain A
         */
        public int[] getHoleA() {
            return holeA;
        }


        /**
         * Method asesor untuk mendapatkan isi dari lobang
         * pada region pemain B
         * @return nilai-nilai pada setiap lobang pemain B
         */
        public int[] getHoleB() {
            return holeB;
        }


        /**
         * Method untuk melakukan pengecekan apakah masih terdapat biji yang dapat
         * dimainkan oleh pemain
         * @param region region dari pemain
         * @return nilai boolean apakah pemain masih dapat memilih rumah dan ada biji yang dapat
         * dimainkan
         */
        public boolean canChoose(String region) {
            int total = 0;
            switch (region) {
                case "A":
                    for(int i=0;i < holeA.length-1; i++) {
                        total+=holeA[i];
                    }
                    break;
                case "B":
                    for(int i=0;i < holeB.length-1; i++) {
                        total+=holeB[i];
                    }
                    break;
            }

            return total > 0;
        }


        /**
         * Method untuk menambahkan nilai dari lobang utama
         * Digunakan ketika terdapat "penembakan" lobang
         * @param region region/nama pemain
         * @param addition jumlah yang ditambahkan
         */
        public void addBigHole(String region, int addition) {
            switch(region) {
                case "A":
                    holeA[holeA.length-1] += addition;break;
                case "B":
                    holeB[holeB.length-1] += addition;break;
            }
        }


        /**
         * Method untuk memilih lobang/rumah yang akan dimainkan
         * @param region region/nama pemain
         * @param hole index lobang/rumah yang dipilih
         */
        public void setHole (String region, int hole) {
            switch(region) {
                case "A" : currentHole = hole; break;
                case "B" : currentHole = HOLE_NUM + hole; break;
            }
        }


        /**
         * Method untuk mengambil semua biji pada lobang yang sekarang ditunjuk pada permainan
         * @return jumlah biji yang diambil
         */
        public int pickHole() {
            int holeVal = getHoleVal();
            setHoleVal(true);
            return holeVal;
        }


        /**
         * Method untuk mengambil semua biji pada lobang tepat sebelum lobang
         * yang sekarang ditunjuk pada permainan
         * @return jumlah biji yang diambil
         */
        public int pickHoleBefore() {
            decrementCurrentHole();
            return pickHole();
        }


        /**
         * Method untuk increment/membersihkan biji pada lobang yang sekarang ditunjuk dalam permainan
         * @param isClear pilihan untuk membersihkan atau increment
         */
        public void setHoleVal(boolean isClear) {
            if(currentHole < HOLE_NUM)  {
                holeA[currentHole]= (isClear) ? 0 : holeA[currentHole]+1;
            } else {
                holeB[currentHole - HOLE_NUM]= (isClear) ? 0 : holeB[currentHole - HOLE_NUM]+1;
            }
            incrementCurrentHole();
        }


        /**
         * Method mutator untuk mengubah jumlah biji dalam lobang sesuai indexnya
         * @param index index lobang, dari 0-15
         * @param val nilai yang diassign
         */
        public void setHoleVal(int index, int val) {
            if(index < HOLE_NUM)  {
                holeA[index]= val;
            } else {
                holeB[index - HOLE_NUM]= val;
            }
        }


        /**
         * Method asesor untuk mengambil jumlah biji pada lobang tertentu sesuai indexnya
         * @param index index lobang, dari 0-15
         * @return jumlah biji yang diambil
         */
        public int getHoleVal(int index) {
            return (index < HOLE_NUM) ? holeA[index]:holeB[index - HOLE_NUM];
        }


        /**
         * Method asesor untuk mengambil jumlah biji pada lobang tertentu
         * sesuai region pemain dan indexnya
         * @param region region pemain
         * @param index index lobang, dari 0-6
         * @return jumlah biji yang diambil
         */
        public int getHoleVal(String region, int index) {
            int idx = 0;
            switch (region) {
                case "A": idx = index;break;
                case "B": idx = index+HOLE_NUM;break;
            }
            return getHoleVal(idx);
        }


        /**
         * Method untuk melakukan mekanisme "Penembakan" pada congklak
         * @param region region/nama pemain
         */
        public void shoot(String region) {
            if(canShoot(region)) {
                addBigHole(region,1);
                int adj = (region.equals("A"))? holeB[HOLE_NUM-2-currentHole] : holeA[2*HOLE_NUM-2-currentHole ];
                addBigHole(region,adj);
                setHoleVal(true);
                decrementCurrentHole();
                int indexAdj = currentHole + (2 * ((HOLE_NUM-1)-currentHole) );
                setHoleVal(indexAdj,0);
            }
        }


        /**
         * Method perbantuan untuk melihat apakah lokasi lobang/rumah sekarang
         * dapat melakukan penembakan
         * @param region region/nama pemain
         * @return nilai boolean apakah pemain dapat melakukan penembakan
         */
        public boolean canShoot(String region) {
            int currentHole = this.currentHole;
            switch (region) {
                case "A" :
                    if(currentHole < HOLE_NUM-1 && holeA[currentHole]==1 && holeB[HOLE_NUM-2-currentHole] > 0) {
                        return true;
                    }
                    break;
                case "B" :
                    if(currentHole > HOLE_NUM-1 && holeB[currentHole-HOLE_NUM]==1 && holeA[2*HOLE_NUM-2-currentHole ] > 0) {
                        return true;
                    }
                    break;
            }
            return false;
        }


        /**
         * Method untuk melihat apakah iterasi pemain sekarang sudah dapat dihentikan
         * @return nilai boolean apakah iterasi dapat dihentikan
         */
        public boolean isStop() {
            boolean ret = getHoleVal()==1;
            return ret;
        }


        /**
         * Method untuk mengambil jumlah biji pada lobang yang sekarang sedang dimainkan
         * @return jumlah biji pada lobang sekarang
         */
        public int getHoleVal() {
            return (currentHole < HOLE_NUM)?holeA[currentHole]:holeB[currentHole - HOLE_NUM];
        }


        /**
         * Method untuk menambahkan jumlah biji pada lobang dan memindahkan index lobang
         * pada posisi berikutnya
         * @param region pemain yang sekarang sedang memainkan permainan
         */
        public void addNext(String region) {
            switch (region) {
                case "B" :
                    if(currentHole!= HOLE_NUM-1) setHoleVal(false);
                    else {
                        incrementCurrentHole();
                        setHoleVal(false);
                    }
                    break;
                case "A" :
                    if(currentHole!= HOLE_NUM*2-1) setHoleVal(false);
                    else {
                        incrementCurrentHole();
                        setHoleVal(false);
                    }
                    break;
            }
        }

        /**
         * Method bantuan untuk menambahkan index lobang/rumah
         * yang sekarang sedang dimainkan
         */
        private void incrementCurrentHole() {
            currentHole++;
            currentHole = currentHole % (HOLE_NUM*2);
        }


        /**
         * Method bantuan untuk mengurangi index lobang/rumah
         * yang sekarang sedang dimainkan
         */
        private void decrementCurrentHole() {
            currentHole--;
            if(currentHole < 0)currentHole = (HOLE_NUM*2-1);
        }
    }

}
