package com.example.courr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Класс FileExtensionRecoveryAppFX представляет собой приложение JavaFX для восстановления расширений файлов
 * в указанном каталоге на основе их содержимого.
 *
 * @author Alexey_Vasyanin
 * @version 1.0
 */
public class FileExtensionRecoveryAppFX extends Application {
    private static final Logger logger = LogManager.getLogger(FileExtensionRecoveryAppFX.class.getName());

    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        logger.info("Starting FileExtensionRecoveryAppFX");
        launch(args);
    }

    /**
     * Переопределенный метод start класса {@code Application} для запуска JavaFX-приложения.
     *
     * @param primaryStage Основная сцена приложения.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Initializing FileExtensionRecoveryAppFX");
            FileExtensionRecoveryController controller = new FileExtensionRecoveryController(primaryStage);
            controller.init();
        } catch (Exception e) {
            logger.error("Error during initialization", e);
        }
    }
}

/**
 * Контроллер для управления основным окном приложения.
 */
class FileExtensionRecoveryController {
    private final Stage primaryStage;

    /**
     * Конструктор контроллера.
     *
     * @param primaryStage Основная сцена приложения.
     */
    public FileExtensionRecoveryController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Инициализация контроллера и отображение основного окна.
     */
    public void init() {
        primaryStage.setTitle("File Extension Recovery");

        FileExtensionRecoveryView view = new FileExtensionRecoveryView(primaryStage);
        view.init();

        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }
}

/**
 * Представление для отображения основного окна приложения.
 */
class FileExtensionRecoveryView {

    private final Stage primaryStage;

    /**
     * Конструктор представления.
     *
     * @param primaryStage Основная сцена приложения.
     */
    public FileExtensionRecoveryView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Инициализация представления и создание основного интерфейса.
     */
    public void init() {
        TextField pathTextField = new TextField();
        Button browseButton = new Button("Browse");
        Button recoverButton = new Button("Recover Extensions");

        // Обработчик события для кнопки "Browse"
        browseButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory");

            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                pathTextField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        // Обработчик события для кнопки "Recover Extensions"
        recoverButton.setOnAction(e -> {
            String folderPath = pathTextField.getText();
            if (folderPath.isEmpty()) {
                // Display an error dialog if the directory is not selected
                new DialogUtil().displayErrorDialog("Please select a directory!");
            } else {
                new FileExtensionRecoveryService().recoverExtensions(folderPath);
                new DialogUtil().displaySuccessDialog("Extensions recovered successfully!");
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        GridPane.setConstraints(pathTextField, 0, 0, 2, 1);
        GridPane.setConstraints(browseButton, 0, 1);
        GridPane.setConstraints(recoverButton, 1, 1);

        gridPane.getChildren().addAll(pathTextField, browseButton, recoverButton);

        Scene scene = new Scene(gridPane, 400, 150);
        primaryStage.setScene(scene);
    }


}

/**
 * Класс для восстановления расширений файлов на основе их содержимого.
 */
class FileExtensionRecoveryService {

    private static final Logger logger = LogManager.getLogger(FileExtensionRecoveryService.class.getName());

    /**
     * Восстанавливает расширения файлов в указанной директории.
     *
     * @param folderPath Путь к целевой директории.
     */
    public void recoverExtensions(String folderPath) {
        logger.info("Recovering extensions in folder: {}", folderPath);
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        logger.info("Opening file: {}", file.getAbsolutePath());
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String extension = detectExtension(bytes);
                        if (extension != null && !extension.isEmpty()) {
                            File newFile = new File(file.getAbsolutePath() + "." + extension);
                            logger.info("Moving file to: {}", newFile.getAbsolutePath());
                            logger.info("Succeed to recover extension for file: {}", file.getAbsolutePath());
                            Files.move(file.toPath(), newFile.toPath());
                        }
                    } catch (IOException e) {
                        logger.error("Error while processing file: {}", file.getAbsolutePath(), e);
                    }
                }
            }
        }
    }

    /**
     * Определяет расширение файла на основе его содержимого.
     *
     * @param bytes Массив байт содержимого файла.
     * @return Расширение файла или null, если не удалось определить.
     */
    public String detectExtension(byte[] bytes) {
        // Implement the extension detection logic here
        String extension = null;
        // Check for specific file types
        if (isJPEG(bytes)) {
            extension = "jpg";
        } else if (isPNG(bytes)) {
            extension = "png";
        } else if (isPDF(bytes)) {
            extension = "pdf";
        } else if (isGIF(bytes)) {
            extension = "gif";
        } else if (isMP3(bytes)) {
            extension = "mp3";
        } else if (isEXE(bytes)) {
            extension = "exe";
        } else if (isZIP(bytes)) {
            extension = "zip";
        } else if (isRAR(bytes)) {
            extension = "rar";
        } else if (isWAV(bytes)) {
            extension = "wav";
        } else if (isICO(bytes)) {
            extension = "ico";
        } else if (isBMP(bytes)) {
            extension = "bmp";
        } else if (isTIFF(bytes)) {
            extension = "tif";
        } else if (isELF(bytes)) {
            extension = "elf";
        } else if (isCLASS(bytes)) {
            extension = "class";
        } else if (isPSD(bytes)) {
            extension = "psd";
        } else if (isISO(bytes)) {
            extension = "iso";
        } else if (isMIDI(bytes)) {
            extension = "midi";
        } else if (is7Z(bytes)) {
            extension = "7z";
        } else if (isMKV(bytes)) {
            extension = "mkv";
        } else if (isXML(bytes)) {
            extension = "xml";
        } else if (isWEBP(bytes)) {
            extension = "webp";
        } else if (isRTF(bytes)) {
            extension = "rtf";
        } else if (isTAR(bytes)) {
            extension = "tar";
        } else if (isAVI(bytes)) {
            extension = "avi";
        } else {
            // General method to get extension
            extension = FilenameUtils.getExtension("tempFile." + FilenameUtils.EXTENSION_SEPARATOR_STR);
        }

        return extension;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением изображения в формате JPEG.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это JPEG-изображение, иначе false.
     */
    private boolean isJPEG(byte[] bytes) {
        return bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением изображения в формате GIF.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это GIF-изображение, иначе false.
     */
    private boolean isGIF(byte[] bytes) {
        return bytes.length >= 6 &&
                bytes[0] == (byte) 0x47 &&
                bytes[1] == (byte) 0x49 &&
                bytes[2] == (byte) 0x46 &&
                bytes[3] == (byte) 0x38 &&
                (bytes[4] == (byte) 0x39 || bytes[4] == (byte) 0x37) &&
                bytes[5] == (byte) 0x61;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением документа в формате PDF.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это PDF-документ, иначе false.
     */
    private boolean isPDF(byte[] bytes) {
        // Проверка, является ли файл PDF
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x25 &&
                bytes[1] == (byte) 0x50 &&
                bytes[2] == (byte) 0x44 &&
                bytes[3] == (byte) 0x46;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением изображения в формате PNG.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это PNG-изображение, иначе false.
     */
    private boolean isPNG(byte[] bytes) {
        return bytes.length >= 8 &&
                bytes[0] == (byte) 0x89 &&
                bytes[1] == 'P' &&
                bytes[2] == 'N' &&
                bytes[3] == 'G' &&
                bytes[4] == (byte) 0x0D &&
                bytes[5] == (byte) 0x0A &&
                bytes[6] == (byte) 0x1A &&
                bytes[7] == (byte) 0x0A;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением аудиофайла в формате MP3.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это MP3-аудио, иначе false.
     */
    private boolean isMP3(byte[] bytes) {
        // Проверка, является ли файл PDF
        return bytes.length >= 2 &&
                ((bytes[0] == (byte) 0x49 &&
                        bytes[1] == (byte) 0x44 &&
                        bytes[2] == (byte) 0x33) || (bytes[0] == (byte) 0xFF &&
                        bytes[1] == (byte) 0xFB) || (bytes[0] == (byte) 0xFF &&
                        bytes[1] == (byte) 0xF3) || (bytes[0] == (byte) 0xFF &&
                        bytes[1] == (byte) 0xF2));
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением исполняемого файла (EXE).
     *
     * @param bytes Массив байтов файла.
     * @return true, если это исполняемый файл (EXE), иначе false.
     */
    private boolean isEXE(byte[] bytes) {
        // Проверка, является ли файл PDF
        return bytes.length >= 2 &&
                bytes[0] == (byte) 0x4D &&
                bytes[1] == (byte) 0x5A;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением архива ZIP.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это ZIP-архив, иначе false.
     */
    private boolean isZIP(byte[] bytes) {
        // Проверка, является ли файл PDF
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x50 &&
                bytes[1] == (byte) 0x4B &&
                bytes[2] == (byte) 0x03 &&
                bytes[3] == (byte) 0x04;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением архива RAR.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это RAR-архив, иначе false.
     */
    private boolean isRAR(byte[] bytes) {
        return bytes.length >= 8 &&
                bytes[0] == (byte) 0x52 &&
                bytes[1] == (byte) 0x61 &&
                bytes[2] == (byte) 0x72 &&
                bytes[3] == (byte) 0x21 &&
                bytes[4] == (byte) 0x1A &&
                bytes[5] == (byte) 0x07 &&
                bytes[6] == (byte) 0x01 &&
                bytes[7] == (byte) 0x00;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением аудиофайла в формате WAV.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это WAV-аудио, иначе false.
     */
    private boolean isWAV(byte[] bytes) {
        return bytes.length >= 12 &&
                bytes[0] == (byte) 0x52 &&
                bytes[1] == (byte) 0x49 &&
                bytes[2] == (byte) 0x46 &&
                bytes[3] == (byte) 0x46 &&
                bytes[8] == (byte) 0x57 &&
                bytes[9] == (byte) 0x41 &&
                bytes[10] == (byte) 0x56 &&
                bytes[11] == (byte) 0x45;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением видеофайла в формате AVI.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это AVI-видео, иначе false.
     */
    private boolean isAVI(byte[] bytes) {
        return bytes.length >= 12 &&
                bytes[0] == (byte) 0x52 &&
                bytes[1] == (byte) 0x49 &&
                bytes[2] == (byte) 0x46 &&
                bytes[3] == (byte) 0x46 &&
                bytes[8] == (byte) 0x41 &&
                bytes[9] == (byte) 0x56 &&
                bytes[10] == (byte) 0x49 &&
                bytes[11] == (byte) 0x20;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением иконки в формате ICO.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это ICO-изображение, иначе false.
     */
    private boolean isICO(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x00 &&
                bytes[1] == (byte) 0x00 &&
                bytes[2] == (byte) 0x01 &&
                bytes[3] == (byte) 0x00;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением изображения в формате BMP.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это BMP-изображение, иначе false.
     */
    private boolean isBMP(byte[] bytes) {
        return bytes.length >= 2 &&
                bytes[0] == (byte) 0x42 &&
                bytes[1] == (byte) 0x4D;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением изображения в формате TIFF.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это TIFF-изображение, иначе false.
     */
    private boolean isTIFF(byte[] bytes) {
        return bytes.length >= 4 &&
                ((bytes[0] == (byte) 0x4D &&
                        bytes[1] == (byte) 0x4D &&
                        bytes[2] == (byte) 0x00 &&
                        bytes[3] == (byte) 0x2A) ||
                        (bytes[0] == (byte) 0x49 &&
                                bytes[1] == (byte) 0x49 &&
                                bytes[2] == (byte) 0x2A &&
                                bytes[3] == (byte) 0x00));
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением исполняемого файла ELF.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это ELF-файл, иначе false.
     */
    private boolean isELF(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x7F &&
                bytes[1] == (byte) 0x45 &&
                bytes[2] == (byte) 0x4C &&
                bytes[3] == (byte) 0x46;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением исполняемого файла Java CLASS.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это Java CLASS-файл, иначе false.
     */
    private boolean isCLASS(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0xCA &&
                bytes[1] == (byte) 0xFE &&
                bytes[2] == (byte) 0xBA &&
                bytes[3] == (byte) 0xBE;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла Adobe Photoshop PSD.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это PSD-файл, иначе false.
     */
    private boolean isPSD(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x38 &&
                bytes[1] == (byte) 0x42 &&
                bytes[2] == (byte) 0x50 &&
                bytes[3] == (byte) 0x53;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате ISO.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате ISO, иначе false.
     */
    private boolean isISO(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x43 &&
                bytes[1] == (byte) 0x44 &&
                bytes[2] == (byte) 0x30 &&
                bytes[3] == (byte) 0x30 &&
                bytes[4] == (byte) 0x31;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате MIDI.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате MIDI, иначе false.
     */
    private boolean isMIDI(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x4D &&
                bytes[1] == (byte) 0x54 &&
                bytes[2] == (byte) 0x68 &&
                bytes[3] == (byte) 0x64;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате 7Z.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате 7Z, иначе false.
     */
    private boolean is7Z(byte[] bytes) {
        return bytes.length >= 6 &&
                bytes[0] == (byte) 0x37 &&
                bytes[1] == (byte) 0x7A &&
                bytes[2] == (byte) 0xBC &&
                bytes[3] == (byte) 0xAF &&
                bytes[4] == (byte) 0x27 &&
                bytes[5] == (byte) 0x1C;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате MKV.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате MKV, иначе false.
     */
    private boolean isMKV(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == (byte) 0x1A &&
                bytes[1] == (byte) 0x45 &&
                bytes[2] == (byte) 0xDF &&
                bytes[3] == (byte) 0xA3;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате XML.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате XML, иначе false.
     */
    private boolean isXML(byte[] bytes) {
        return bytes.length >= 6 &&
                bytes[0] == (byte) 0x3C &&
                bytes[1] == (byte) 0x3F &&
                bytes[2] == (byte) 0x78 &&
                bytes[3] == (byte) 0x6D &&
                bytes[4] == (byte) 0x6C &&
                bytes[5] == (byte) 0x20;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате RTF.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате RTF, иначе false.
     */
    private boolean isRTF(byte[] bytes) {
        return bytes.length >= 6 &&
                bytes[0] == (byte) 0x7B &&
                bytes[1] == (byte) 0x5C &&
                bytes[2] == (byte) 0x72 &&
                bytes[3] == (byte) 0x74 &&
                bytes[4] == (byte) 0x66 &&
                bytes[5] == (byte) 0x31;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате WEBP.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате WEBP, иначе false.
     */
    private boolean isWEBP(byte[] bytes) {
        return bytes.length >= 12 &&
                bytes[0] == (byte) 0x52 &&
                bytes[1] == (byte) 0x49 &&
                bytes[2] == (byte) 0x46 &&
                bytes[3] == (byte) 0x46 &&
                bytes[8] == (byte) 0x57 &&
                bytes[9] == (byte) 0x45 &&
                bytes[10] == (byte) 0x42 &&
                bytes[11] == (byte) 0x50;
    }

    /**
     * Проверяет, является ли переданный массив байтов представлением файла в формате TAR.
     *
     * @param bytes Массив байтов файла.
     * @return true, если это файл в формате TAR, иначе false.
     */
    private boolean isTAR(byte[] bytes) {
        return bytes.length >= 8 &&
                ((bytes[0] == (byte) 0x75 &&
                        bytes[1] == (byte) 0x73 &&
                        bytes[2] == (byte) 0x74 &&
                        bytes[3] == (byte) 0x61 &&
                        bytes[4] == (byte) 0x72 &&
                        bytes[5] == (byte) 0x00 &&
                        bytes[6] == (byte) 0x30 &&
                        bytes[7] == (byte) 0x30) || (bytes[0] == (byte) 0x75 &&
                        bytes[1] == (byte) 0x73 &&
                        bytes[2] == (byte) 0x74 &&
                        bytes[3] == (byte) 0x61 &&
                        bytes[4] == (byte) 0x72 &&
                        bytes[5] == (byte) 0x20 &&
                        bytes[6] == (byte) 0x20 &&
                        bytes[7] == (byte) 0x00));
    }
}

/**
 * Утилитарный класс для отображения диалоговых окон в JavaFX.
 * Использует библиотеку логирования Log4j для записи событий.
 */
class DialogUtil {
    private static final Logger logger = LogManager.getLogger(DialogUtil.class.getName());

    /**
     * Отображает диалоговое окно с информационным сообщением об успехе.
     *
     * @param message Сообщение об успехе, которое будет отображено в диалоге.
     */
    public void displaySuccessDialog(String message) {
        logger.info("Displaying success dialog: {}", message);
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Отображает диалоговое окно с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке, которое будет отображено в диалоге.
     */
    public void displayErrorDialog(String message) {
        logger.error("Displaying error dialog: {}", message);
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
