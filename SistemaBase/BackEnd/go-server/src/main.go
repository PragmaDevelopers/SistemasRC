package main

import (
	"database/sql"
	//"errors"
	"fmt"
	"math/rand"
	"net/http"
	"time"

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
	ginRetFunc := func(c *gin.Context) {
		var kanbanData Kanban
		if err := c.ShouldBindJSON(&kanbanData); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}

		fmt.Println(kanbanData)

		//db.Exec(`
		//	INSERT INTO dashboards (name, dashid, columns) VALUES ($1, $2, $3)
		//`, kanbanData.Name, kanbanData.ID, kanbanData.Columns)
	}

	return ginRetFunc
}

func initDatabase(db *sql.DB) error {
	_, err := db.Exec(`
		CREATE TABLE IF NOT EXISTS dashboards (
			dbid	SERIAL			PRIMARY		KEY,
			name	TEXT,
			dashid	TEXT,
			columns	VARCHAR(32)[]
		)
	`)

	return err
}

func main() {
	db, err := sql.Open("postgres", "user=mirai dbname=myDatabaseName sslmode=disable")
	router := gin.Default()
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

	// Kanban End-points
	router.POST("/dashboard/create", createKanban(db))

	router.Run()

}
