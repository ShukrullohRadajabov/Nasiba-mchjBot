package org.example.Servise;

import org.example.Entity.Basket;
import org.example.Entity.Photo;
import org.example.TelegramBot.NasibaMCHJ;
import org.example.Entity.User;
import org.example.TelegramBot.Token;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.example.Button.ButtonArray.*;
import static org.example.Button.ButtonName.*;
import static org.example.Databases.Database.*;
import static org.example.Databases.DatabaseConnection.connection;
import static org.example.State.UserState.*;

public class ButtonServise {
	MessengerService messengerService = new MessengerService();

	//  ------------------------  CHECK USER AND REGISTER SECTION  ----------------------------------------
	public void Register(Update update, User user, SendMessage sendMessage) throws SQLException, ClassNotFoundException {
		String text = update.getMessage().getText();
		switch (user.getExitState()) {

			case NAME -> {
				if (text.length() > 3) {
					String fristName = update.getMessage().getText();
					user.setFirstName(fristName);
					user.setExitState(NUMBER);
					Statement statement = connection().createStatement();
					 statement.execute("update users set exit_state='" + NUMBER + "'" +
							",first_name= '"+fristName+"' where chat_id='" + update.getMessage().getChatId() + "'");

					sendMessage.setText("Telefon raqamingizni kiriting(+998XXYYYYYYY)");
				} else {
					sendMessage.setText(" Xato ma'lumot! \n\n Qaytadan ismingizni kiriting");
				}
			}
			case NUMBER -> {
				if (text.matches("\\+998\\d{9}")) {
					user.setContact(text);
					sendMessage.setText("""
							Yashash manzilingni kiriting
							(Misol: Samarqand viloyati Samarqand shahri)
							""");
					user.setExitState(REGION);
					Statement statement = connection().createStatement();
					statement.execute("update users set exit_state='" + REGION + "',phone_number='"+text+"'" +
							" where chat_id='" + update.getMessage().getChatId() + "'");
				} else {
					sendMessage.setText("Noto'gri raqam kiritdingiz \n\n Iltimos qaytadan kiriting(+998XXYYYYYYY)");
				}
			}
			case REGION -> {
				if (text.length() > 5) {
					user.setRegion(text);
					sendMessage.setText("Tug'ilgan sanangizni kiriting:\n(Misol: 2023.03.23) ");
					user.setExitState(DATE_OF_BIRTH);
					Statement statement = connection().createStatement();
					statement.execute("update users set exit_state='" + DATE_OF_BIRTH + "',region='"+text+"'" +
							" where chat_id='" + update.getMessage().getChatId() + "'");


				} else sendMessage.setText("Noto'g'ri manzil kiritdingiz \n\n Iltimos qaytadan kiriting");
			}
			case DATE_OF_BIRTH -> {
				if (text.length() > 7) {
					user.setDateOfBirth(text);
					sendMessage.setText("📨 Ma'lumotlaringiz qabul qilindi.\n⏳ Tez orada sizga dasturdan foydalanishlik uchun ruxsat beriladi. ");
					user.setExitState(PERMISSION);
					user.setTelegramUsername(update.getMessage().getFrom().getUserName());
					try {
					Statement statement = Objects.requireNonNull(connection()).createStatement();
					statement.execute("update users set exit_state='" + PERMISSION + "',birth_date='"+text+"'," +
							"telegram_username ='"+update.getMessage().getFrom().getUserName()+"'" +
							" where chat_id='" + update.getMessage().getChatId() + "'");
					}catch (SQLException e){
						e.printStackTrace();
						sendMessage.setChatId(user.getChatId());
						sendMessage.setText("Tug'ilgan sanangizni kiritishda xatolik :\n(Misol: 1900.01.01 )");
					}
					addNewUser(user);
				} else sendMessage.setText("Noto'g'ri ma'lumot kiritdingiz \n\n Iltimos qaytadan kiriting");
			}

			case PERMISSION -> {
				sendMessage.setText("🕑 Iltimos kutib turing");
				System.out.println(user.getChatId());

			}

		}
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addNewUser(User user) {
		SendMessage sendMessage1 = new SendMessage();
		sendMessage1.setChatId(Token.BOT_ADMIN_ID);
		sendMessage1.setText(user.toString());

		InlineKeyboardButton button = new InlineKeyboardButton("Ruxsat berish");
		button.setCallbackData(user.getChatId()+" ruxsat");
		InlineKeyboardButton button2 = new InlineKeyboardButton("Rad etish");
		button2.setCallbackData(user.getChatId() + " radEtish");

		List<InlineKeyboardButton> row = List.of(button, button2);

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(List.of(row));

		sendMessage1.setReplyMarkup(inlineKeyboardMarkup);

		new NasibaMCHJ().sendMsg(sendMessage1);
	}

	//---------------------------------  ADD NEW PHOTO SECTION  -----------------------------------------
	public void addPantalonPhoto(User admin, SendMessage sendMessage) {
		admin.setState(ADD_PANTALON);
		messengerService.dbConnectionState(admin);

		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
//        String[][] exit = {{EXIT}};
		System.out.println("\"keldi\" = " + "keldi");
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addQimmatPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_QIMMAT);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addArzonPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_ARZON);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addMensPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_MENS);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addMaykalarPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_MAYKALAR);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addTopikPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_TOPIK);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addBandajPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_BANDAJ);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addPadraskoviPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_PADRASKOVI);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addUgilBolaPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_UGIL_BOLA);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addQizBolaLastichkaPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_QIZ_BOLA_LASTCHKA);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addQizBolaShortikPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_QIZ_BOLA_SHORTIK);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addMersPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_MERS);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addSalesGoodsPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_SALES_GOODS);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addAllWomenGoodsPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_ALL_WOMEN_GOODS);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}
	public void addNewGoodsPhoto(User admin, SendMessage sendMessage) {
		sendMessage.setChatId(admin.getChatId());
		sendMessage.setText("Rasimni tashlang");
		String[][] exit = {{EXIT}};
		sendMessage.setReplyMarkup(messengerService.getKeyboard(exit));
		admin.setState(ADD_NEW_GOODS);
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().sendMsg(sendMessage);
	}


	//--------------------------------  PHOTO CHECK SECTION  --------------------------------------------
	public void checkPantalonPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);

		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "pantalon");
			pantalon.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Pantalonlar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkQimmatPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "qimmat");
			qimmat.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Arginal mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
			} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkArzonPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "arzon");
			arzon.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Arzon mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkMaykalarPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "Maykalar");
			Maykalar.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Maykalar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkMensPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			System.out.println(photo.get(photo.size() - 1).getFilePath());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1,"mens");

			mens.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Mujski mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkTopikPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "topik");
			topik.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Topiklar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkBandajPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "bandaj");
			bandaj.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Bandajlar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkPadraskoviPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "padraskovi");
			padraskovi.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Padraskovi mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkUgilBolaPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);

			basket(caption, photo1, "ugilBola");
			ugilBola.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("O'g'il bola bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkQizBolaLastichkaPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "qizBolaLastichka");
			qizBolaLastichka.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Qiz bola lastichka bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkQizBolaShortikPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "qizBolaShortik");
			qizBolaShortik.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Qiz bola shortik bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkMersPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);

			basket(caption, photo1, "Merslar");
			Merslar.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Merslar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}

	}
	public void checkSalesGoodsPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);

			basket(caption, photo1, "sales");
			sales.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Chegirmali mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkAllWomenGoodsPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);
			basket(caption, photo1, "allWomenGoods");
			allWomenGoods.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Umumiy mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	public void checkNewGoodsPhoto(Update update, User admin, SendMessage sendMessage) {
		if (update.getMessage().hasPhoto()) {
			String caption = update.getMessage().getCaption();
			List<PhotoSize> photo = update.getMessage().getPhoto();
			Photo photo1 = new Photo();
			photo1.setId(photoCountId());
			photo1.setFileId(photo.get(photo.size() - 1).getFileId());
			photo1.setCaption(caption);

			basket(caption, photo1, "newGoods");
			newGoods.add(photo1);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Yangi mollar bo'limiga " + photo1.getProductCode() + " qo'shildi");
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (update.getMessage().getText().equals(EXIT)) {
			admin.setState(ADD_GOODS);
			messengerService.dbConnectionState(admin);
			sendMessage.setText("Bo'limni tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMenu));
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}

	// --------------------------------------  DELETE SECTION--------------------------------------------------------------

	public void deleteQimmatPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_QIMMAT, qimmat);
	}
	public void deleteMensPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_MENS, mens);
	}
	public void deleteArzonPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_ARZON, arzon);
	}
	public void deletePantalonPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_PANTALON, pantalon);

	}
	public void deleteMaykalarPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_MAYKALAR, Maykalar);
	}
	public void deleteTopikPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_TOPIK, topik);
	}
	public void deletePadraskoviPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_PADRASKOVI, padraskovi);
	}
	public void deleteUgilBolaPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_UGIL_BOLA, ugilBola);
	}
	public void deleteQizBolaLastichkaPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_QIZ_BOLA_LASTCHKA, qizBolaLastichka);
	}
	public void deleteQizBolaShortikPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_QIZ_BOLA_SHORTIK, qizBolaShortik);
	}
	public void deleteMersPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_MERS, Merslar);
	}
	public void deleteSalesGoodsPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_SALES_GOODS, sales);
	}
	public void deleteAllWomenGoodsPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_ALL_WOMEN_GOODS, allWomenGoods);
	}
	public void deleteNewGoodsPhoto(Update update, User admin, SendMessage sendMessage) {
		messengerService.dbConnectionState(admin);
		new NasibaMCHJ().adminDeleteGoods(update, DELETE_NEW_GOODS, newGoods);
	}
	public void showUserId(Update update, User admin, SendMessage sendMessage) {
		String text = update.getMessage().getText();
		if (text.equals(EXIT)){
			admin.setState(NEW);
			messengerService.dbConnectionState(admin);
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Bo'lim tanlang");
			sendMessage.setReplyMarkup(messengerService.getKeyboard(adminMarkup));
			new NasibaMCHJ().sendMsg(sendMessage);
		} else if (text.matches("^[0-9]+$")) {
			for (User value : userHashMap.values()) {
				if (value.getId()==(Integer.parseInt(text))){
						sendMessage.setText(value.toString());
						sendMessage.setReplyMarkup(inlineShowUserButton(value));
					for (int i = 0; i < 2; i++) {
						sendMessage.setChatId(admin.getChatId());
						new NasibaMCHJ().sendMsg(sendMessage);
						sendMessage.setText("Tanlang");
						sendMessage.setReplyMarkup(messengerService.getKeyboard(exitadmin));
					}
						return;
				}
			}
		}else {
			sendMessage.setChatId(admin.getChatId());
			sendMessage.setText("Id kritishda xatolik \n Qaytadan idni kiriting.");
			new NasibaMCHJ().sendMsg(sendMessage);
		}
	}
	private ReplyKeyboard inlineShowUserButton(User value) {
		InlineKeyboardButton button = new InlineKeyboardButton("Buyurtmalari");
		button.setCallbackData(value.getChatId() + " userBuyurma");
		InlineKeyboardButton button1 = new InlineKeyboardButton("Bloklash");
		button1.setCallbackData(value.getChatId() + " userBloc");
		InlineKeyboardButton button2 = new InlineKeyboardButton("Faollashtrish");
		button2.setCallbackData(value.getChatId() + " userActive");
		if (value.isActive()) {
			List<InlineKeyboardButton> row = List.of(button,button1);
			return new InlineKeyboardMarkup(List.of(row));
		}else {
			List<InlineKeyboardButton> row = List.of(button,button2);
		return new InlineKeyboardMarkup(List.of(row));
		}
	}

	// ---------------------------------------  CHECK CAPTION  ---------------------------------------------------------------
	public void basket(String caption, Photo photo1, String name) {
		boolean r = caption.contains("Razmer:");
		boolean q = caption.contains(";");
		int index = caption.indexOf("Code:");
		int index1 = caption.indexOf("Narx:");
		int index2 = caption.indexOf("so’m");
		if(r){
			int sizeIndex = caption.indexOf("Razmer:") + 7;
			String size = caption.substring(sizeIndex, index);
			photo1.setSize(size.trim());
		}
		String code = caption.substring(index + 6, index1 - 1);
		String price = ((caption.substring(index1 + 5, index2 - 1)).trim());
		if(q){
			int ind = caption.indexOf(";");
			price =  ((caption.substring(index1 + 5, ind)).trim());
		}

		if(price.equals("Razmer")){
			photo1.setCategory_id(2);
			price = "0";
			/*int code1Index = caption.indexOf("Code:") + 6;
			int price1Index = caption.indexOf("Narx:");
			int made1Index = caption.indexOf("MADE");
			int razmerIndex = caption.indexOf("Razmer");
			String productCode = caption.substring(code1Index, price1Index);
			String f = caption.substring(razmerIndex + 7, made1Index);
			String[] f1 = f.split("\\n");
			for (String s : f1) {
				if (s.contains("so’m")) {
					int f_sum = s.indexOf("so’m");
					String pair = s.substring(0, f_sum);
					String[] splits = pair.split(" ");
					String size = splits[0];
					String xlProductPrice = splits[1];
					String replacecaption = caption.replace('\'', '\"');
					photo1.setProductCode(productCode.trim());
					photo1.setSize(size);
					basketList.add(new Basket(productCode, Long.parseLong(xlProductPrice), photo1.getId(), 0, photo1.getSize()));
					try {
						connection().createStatement().execute("insert into photo (id,file_id, caption, product_code, category_name, size)" +
								" VALUES " +
								"('"+ new Random().nextInt() +"','"+photo1.getFileId()+"','"+replacecaption+"','"+productCode+"','"+name+"','"+photo1.getSize()+"')");
						connection().close();
						connection().createStatement().execute("insert into basket (product_code, price, photo_id, count, size)" +
								" VALUES " +
								"('"+productCode+"','"+xlProductPrice+"','"+photo1.getId()+"','"+0+"','"+photo1.getSize()+"')");
					} catch (Exception  e) {
						e.printStackTrace();
					}
				}
			}
			for (Basket basket : basketList) {
				System.out.println(basket);
			}
			return;*/
		}

		String replacecaption = caption.replace('\'', '\"');
		photo1.setProductCode(code.trim());
		basketList.add(new Basket(code, Long.parseLong(price), photo1.getId(), 0, photo1.getSize()));
		try {
			connection().createStatement().execute("insert into photo (id,file_id, caption, product_code, category_name, size, category_id)" +
					" VALUES " +
					"('"+photo1.getId()+"','"+photo1.getFileId()+"','"+replacecaption+"','"+code+"','"+name+"','"+photo1.getSize()+"', '"+photo1.getCategory_id()+"')");
			connection().createStatement().execute("insert into basket (product_code, price, photo_id, count, size)" +
					" VALUES " +
					"('"+code+"','"+price+"','"+photo1.getId()+"','"+0+"','"+photo1.getSize()+"')");
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
}



