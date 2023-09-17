package main

import (
	"database/sql"
	//"errors"
	"fmt"
	"math/rand"
	"net/http"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/lib/pq"
)

type CheckListItem struct {
	Name        string `json:"name"`
	ChecklistID string `json:"id"`
	Completed   bool   `json:"completed"`
}

type CheckList struct {
	Name  string          `json:"name"`
	ID    string          `json:"id"`
	Items []CheckListItem `json:"items"`
}

type Card struct {
	Title       string      `json:"title"`
	ID          string      `json:"id"`
	ColumnID    string      `json:"columnID"`
	Description string      `json:"description"`
	Checklists  []CheckList `json:"checklists"`
}

type Column struct {
	Title      string `json:"title"`
	ID         string `json:"id"`
	ColumnType uint8  `json:"columnType"`
	CardsList  []Card `json:"cardsList"`
}

type Kanban struct {
	ID      string   `json:"kanbanId"`
	Columns []Column `json:"columns"`
	Name    string   `json:"name"`
}

func generateRandomString() string {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))

	characters := "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
	result := make([]byte, 32)
	format := map[int]bool{10: true, 21: true}

	for i := 0; i < 32; i++ {
		if format[i] {
			result[i] = '-'
		} else {
			result[i] = characters[r.Intn(len(characters))]
		}
	}

	return string(result)
}

func createKanban(db *sql.DB) gin.HandlerFunc {
	return func(c *gin.Context) {
		var kanbanData Kanban
		if err := c.ShouldBindJSON(&kanbanData); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}

		fmt.Println(kanbanData)

		db.Exec(`
			INSERT INTO dashboards (name, dashid, columns) VALUES ($1, $2, $3)
		`, kanbanData.Name, kanbanData.ID, pq.Array(kanbanData.Columns))
		fmt.Println("KANBAN DATA INSERTED INTO dashboards TABLE")
	}
}

func getAllKanbans(db *sql.DB) gin.HandlerFunc {
	return func(c *gin.Context) {
		rows, err := db.Query("SELECT * FROM dashboards")
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
			return
		}
		defer rows.Close()

		for rows.Next() {
			var ID int
			var name, dashid string
			var columns []string

			err := rows.Scan(&ID, &name, &dashid, pq.Array(&columns))
			if err != nil {
				panic(err)
			}
			fmt.Printf("TABLE DATA\n\tID:\t%d\n\tName:\t%s\n\tDASHID:\t%s\n\tCOLUMNS:\t%v\n", ID, name, dashid, columns)
		}
		if err = rows.Err(); err != nil {
			panic(err)
		}
	}
}

func initDatabase(db *sql.DB) error {
	_, err := db.Exec(`CREATE TABLE kanban (
    kanban_id UUID PRIMARY KEY,
    name TEXT
);
`)
	_, err = db.Exec(`CREATE TABLE column (
    column_id UUID PRIMARY KEY,
    kanban_id UUID REFERENCES kanban(kanban_id),
    title TEXT,
    column_type INT
);
`)
	_, err = db.Exec(`CREATE TABLE card (
    card_id UUID PRIMARY KEY,
    column_id UUID REFERENCES column(column_id),
    title TEXT,
    description TEXT
);
`)
	_, err = db.Exec(`CREATE TABLE checklist (
    checklist_id UUID PRIMARY KEY,
    card_id UUID REFERENCES card(card_id),
    name TEXT
);
`)
	_, err = db.Exec(`CREATE TABLE checklist_item (
    item_id UUID PRIMARY KEY,
    checklist_id UUID REFERENCES checklist(checklist_id),
    name TEXT,
    completed BOOLEAN
);
`)

	return err
}

func main() {
	db, err := sql.Open("postgres", "user=mirai dbname=myDatabaseName sslmode=disable")
	router := gin.Default()
	router.Use(cors.Default())
	if err != nil {
		panic(err)
	}
	defer db.Close()

	err = db.Ping()
	if err != nil {
		panic(err)
	}

	err = initDatabase(db)
	if err != nil {
		panic(err)
	}

	fmt.Println("Connected to PostgreSQL database")

	// Kanban End-points
	router.POST("/dashboard/create", createKanban(db))
	router.GET("/dashboard/getall", getAllKanbans(db))

	router.Run()

}
