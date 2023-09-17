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
	_ "github.com/lib/pq"
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
	KanbanID   string `json:"kanbanId"`
}

type Kanban struct {
	ID   string `json:"kanbanId"`
	Name string `json:"name"`
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

func initDatabase(db *sql.DB) {
	createTableQueries := []string{
		`
        CREATE TABLE IF NOT EXISTS kanban (
            kanban_id VARCHAR(32) PRIMARY KEY,
            name TEXT
        )`,
		`
        CREATE TABLE IF NOT EXISTS columns_data (
            column_id VARCHAR(32) PRIMARY KEY,
            kanban_id VARCHAR(32) REFERENCES kanban(kanban_id),
            title TEXT,
            column_type INT
        )`,
		`
        CREATE TABLE IF NOT EXISTS card (
            card_id VARCHAR(32) PRIMARY KEY,
            column_id VARCHAR(32) REFERENCES columns_data(column_id),
            title TEXT,
            description TEXT
        )`,
		`
        CREATE TABLE IF NOT EXISTS checklist (
            checklist_id VARCHAR(32) PRIMARY KEY,
            card_id VARCHAR(32) REFERENCES card(card_id),
            name TEXT
        )`,
		`
        CREATE TABLE IF NOT EXISTS checklist_item (
            item_id VARCHAR(32) PRIMARY KEY,
            checklist_id VARCHAR(32) REFERENCES checklist(checklist_id),
            name TEXT,
            completed BOOLEAN
        )`,
	}

	for _, query := range createTableQueries {
		_, err := db.Exec(query)
		if err != nil {
			panic(err)
		}
		fmt.Println("Table created successfully.")
	}
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
			INSERT INTO kanban (kanban_id, name) VALUES ($1, $2)
		`, kanbanData.ID, kanbanData.Name)
		fmt.Println("KANBAN DATA INSERTED INTO kanban TABLE")
	}
}

func getAllKanbans(db *sql.DB) gin.HandlerFunc {
	return func(c *gin.Context) {
		rows, err := db.Query("SELECT * FROM kanban")
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
			return
		}
		defer rows.Close()

		var Kanbans []Kanban

		for rows.Next() {
			var kanban_id string
			var name string

			err := rows.Scan(&kanban_id, &name)
			if err != nil {
				panic(err)
			}
			fmt.Printf("TABLE DATA\n\tID:\t%s\n\tName:\t%s\n", kanban_id, name)
			Kanbans = append(Kanbans, Kanban{Name: name, ID: kanban_id})
		}

		fmt.Println(Kanbans)

		if err = rows.Err(); err != nil {
			panic(err)
		}

		c.JSON(http.StatusOK, Kanbans)
	}
}

func createColumn(db *sql.DB) gin.HandlerFunc {
	return func(c *gin.Context) {
		var columnData Column
		if err := c.ShouldBindJSON(&columnData); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		// ACCESS PARAMETERS FROM CONTEXT (not really necessary tho, it works without them)
		fmt.Println(columnData)

		db.Exec(`
			INSERT INTO columns_data (id, title, column_type, kanban_id) VALUES ($1, $2, $3, $4)
		`, columnData.ID, columnData.Title, columnData.ColumnType, columnData.KanbanID)
		fmt.Println("COLUMN DATA INSERTED INTO columns_data TABLE")

	}
}

func getColumn(db *sql.DB) gin.HandlerFunc {
	return func(c *gin.Context) {
		kanbanId := c.Param("kanbanid")
		fmt.Println(kanbanId)
	}
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

	initDatabase(db)

	fmt.Println("Connected to PostgreSQL database")

	// Kanban End-Points
	router.POST("/api/dashboard/kanban/create", createKanban(db))
	router.GET("/api/dashboard/kanban/getall", getAllKanbans(db))

	// Columns End-Points
	router.POST("/api/dashboard/column/create/:kanbanid", createColumn(db))
	router.GET("/api/dashboard/column/getall/:kanbanid", getColumn(db))

	router.Run()

}
