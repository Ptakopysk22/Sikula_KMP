package cz.bosan.sikula_kmp.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import cz.bosan.sikula_kmp.app.AppViewModel
import cz.bosan.sikula_kmp.managers.camp_manager.data.CampDataSource
import cz.bosan.sikula_kmp.managers.camp_manager.data.DefaultCampRepository
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildDataSource
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildRepository
import cz.bosan.sikula_kmp.core.data.HttpClientFactory
import cz.bosan.sikula_kmp.managers.leader_manager.data.DefaultLeaderRepository
import cz.bosan.sikula_kmp.features.home.HomeViewModel
import cz.bosan.sikula_kmp.managers.leader_manager.data.LeaderDataSource
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.user_manager.UserDataSource
import cz.bosan.sikula_kmp.managers.user_manager.UserRepository
import cz.bosan.sikula_kmp.core.data.DatabaseFactory
import cz.bosan.sikula_kmp.core.data.ExternalHttpClientFactory
import cz.bosan.sikula_kmp.core.data.LocalDB
import cz.bosan.sikula_kmp.core.data.SimpleTokenRefreshService
import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.data.TokenHolder
import cz.bosan.sikula_kmp.core.data.TokenManager
import cz.bosan.sikula_kmp.core.data.TokenRefreshService
import cz.bosan.sikula_kmp.features.about_app.AboutAppViewModel
import cz.bosan.sikula_kmp.features.attendee_management.AttendeeManagerViewModel
import cz.bosan.sikula_kmp.features.attendee_management.attendee_list.AttendeeListViewModel
import cz.bosan.sikula_kmp.features.attendee_management.check_user.CheckUserViewModel
import cz.bosan.sikula_kmp.features.attendee_management.child_detail.ChildDetailViewModel
import cz.bosan.sikula_kmp.features.attendee_management.children_list.ChildrenListViewModel
import cz.bosan.sikula_kmp.features.attendee_management.leader_detail.LeaderDetailViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_record_list.BoatRaceRecordListViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_recording.BoatRaceRecordingViewModel
import cz.bosan.sikula_kmp.features.discipline_management.child_records.ChildRecordsViewModel
import cz.bosan.sikula_kmp.features.discipline_management.count_recoding_team_discipline.CountRecordingTeamDisciplineViewModel
import cz.bosan.sikula_kmp.features.discipline_management.count_recording_individual_discipline.CountRecordingViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.crew_records.CrewRecordsViewModel
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.morning_exercise_hub.MorningExerciseHubViewModel
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.record_list.RecordListViewModel
import cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording.TrailRecordingViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list.BadgesListViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.badges_list_granting.BadgesListGrantingViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_discipline_list.DailyDisciplineListViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.daily_team_discipline_list.DailyTeamDisciplineRecordListViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.dual_option_list.DualOptionListViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_all_records.NegativePointsAllRecordsViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_recording.NegativePointsRecordingViewModel
import cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.position_discipline_hub.PositionDisciplinesHubViewModel
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_detail.ConsumerDetailViewModel
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_list.ConsumerListViewModel
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.edit_deposit.EditDepositViewModel
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_detail.ProductDetailViewModel
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_list.ProductListViewModel
import cz.bosan.sikula_kmp.features.points_management.crew_discipline_points.CrewDisciplinePointsViewModel
import cz.bosan.sikula_kmp.features.points_management.crew_points.CrewPointsViewModel
import cz.bosan.sikula_kmp.features.points_management.discipline_points.DisciplinePointsViewModel
import cz.bosan.sikula_kmp.features.points_management.point_dicipline_record_list.PointDisciplineRecordListViewModel
import cz.bosan.sikula_kmp.features.points_management.points_hub.PointsHubViewModel
import cz.bosan.sikula_kmp.features.signin.SignInViewModel
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data.BadgesDataSource
import cz.bosan.sikula_kmp.managers.discipline_manager.badgesManager.data.BadgesRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineRecordRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualDisciplineDataSource
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoDataSource
import cz.bosan.sikula_kmp.managers.discipline_manager.reviewManager.data.ReviewInfoRepository
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineDataSource
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamDisciplineRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerDataSource
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager.ExternalLimoCounterDataSource
import cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager.GeneralLimoCounterDataSource
import cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager.GeneralLimoCounterRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.ProductDataSource
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.ProductRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager.TransactionDataSource
import cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager.TransactionRepository
import cz.bosan.sikula_kmp.managers.points_manager.data.PointsDataSource
import cz.bosan.sikula_kmp.managers.points_manager.data.PointsRepository
import cz.bosan.sikula_kmp.managers.server_manager.ServerDataSource
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<FirebaseAuth> { Firebase.auth }

    single { HttpClientFactory.create(get()) }
    single(named("ExternalClient")) { ExternalHttpClientFactory.create(get()) }

    single { TokenHolder }

    single(named("RefreshClient")) {
        HttpClientFactory.create(get())
    }

    single<TokenRefreshService> {
        SimpleTokenRefreshService(get(named("RefreshClient")))
    }

    single(createdAtStart = false) {
        TokenManager(get(), lazy { get<DefaultLeaderRepository>() })
    }

    single {
        TokenAwareHttpClient(get(), lazy { get<TokenManager>() })
    }



    singleOf(::ServerDataSource)
    singleOf(::ServerRepository)
    singleOf(::LeaderDataSource)
    singleOf(::DefaultLeaderRepository).bind<LeaderRepository>()
    singleOf(::CampDataSource)
    singleOf(::DefaultCampRepository).bind<CampRepository>()
    singleOf(::UserDataSource)
    singleOf(::UserRepository)
    singleOf(::ChildDataSource)
    singleOf(::ChildRepository)
    singleOf(::IndividualDisciplineDataSource)
    singleOf(::IndividualDisciplineRecordRepository)
    singleOf(::ReviewInfoDataSource)
    singleOf(::ReviewInfoRepository)
    singleOf(::TeamDisciplineDataSource)
    singleOf(::TeamDisciplineRepository)
    singleOf(::BadgesDataSource)
    singleOf(::BadgesRepository)
    singleOf(::PointsDataSource)
    singleOf(::PointsRepository)
    singleOf(::ConsumerDataSource)
    singleOf(::ConsumerRepository)
    single {
        ExternalLimoCounterDataSource(get(named("ExternalClient")))
    }
    singleOf(::GeneralLimoCounterDataSource)
    singleOf(::GeneralLimoCounterRepository)
    singleOf(::ProductDataSource)
    singleOf(::ProductRepository)
    singleOf(::TransactionDataSource)
    singleOf(::TransactionRepository)


    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<LocalDB>().currentLeaderDao }
    single { get<LocalDB>().individualRecordDao }
    single { get<LocalDB>().childDao }
    single { get<LocalDB>().trailCategoryDao }
    single { get<LocalDB>().leaderDao }
    single { get<LocalDB>().crewDao }
    single { get<LocalDB>().teamRecordDao }

    single { AttendeeManagerViewModel() }

    viewModelOf(::AppViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AttendeeListViewModel)
    viewModelOf(::CheckUserViewModel)
    viewModelOf(::LeaderDetailViewModel)
    viewModelOf(::ChildrenListViewModel)
    viewModelOf(::ChildDetailViewModel)
    viewModelOf(::MorningExerciseHubViewModel)
    viewModelOf(::CountRecordingViewModel)
    viewModelOf(::RecordListViewModel)
    viewModelOf(::ChildRecordsViewModel)
    viewModelOf(::TrailRecordingViewModel)
    viewModelOf(::BoatRaceRecordListViewModel)
    viewModelOf(::BoatRaceRecordingViewModel)
    viewModelOf(::CrewRecordsViewModel)
    viewModelOf(::PositionDisciplinesHubViewModel)
    viewModelOf(::DualOptionListViewModel)
    viewModelOf(::DailyDisciplineListViewModel)
    viewModelOf(::NegativePointsRecordingViewModel)
    viewModelOf(::BadgesListViewModel)
    viewModelOf(::BadgesListGrantingViewModel)
    viewModelOf(::DailyTeamDisciplineRecordListViewModel)
    viewModelOf(::CountRecordingTeamDisciplineViewModel)
    viewModelOf(::PointsHubViewModel)
    viewModelOf(::DisciplinePointsViewModel)
    viewModelOf(::CrewPointsViewModel)
    viewModelOf(::CrewDisciplinePointsViewModel)
    viewModelOf(::PointDisciplineRecordListViewModel)
    viewModelOf(::ConsumerListViewModel)
    viewModelOf(::ConsumerDetailViewModel)
    viewModelOf(::EditDepositViewModel)
    viewModelOf(::ProductListViewModel)
    viewModelOf(::ProductDetailViewModel)
    viewModelOf(::AboutAppViewModel)
    viewModelOf(::NegativePointsAllRecordsViewModel)
}